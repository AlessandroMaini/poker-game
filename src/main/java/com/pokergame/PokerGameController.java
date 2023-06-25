package com.pokergame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

public class PokerGameController {
    final public int CARD_HEIGHT = 145;
    final public int CARD_WIDTH = 100;
    final public static String IMAGE_DIRECTORY = "./src/main/resources/com/pokergame/cards";
    final public int NUM_PLAYERS = 4;
    final public static long BLIND = 20L;
    public Deck deck;
    public Pot pot;
    public Player[] players = new Player[NUM_PLAYERS];
    public PlayerHand[] hands = new PlayerHand[NUM_PLAYERS];
    public PlayerBet[] bets = new PlayerBet[NUM_PLAYERS];
    public CommunityCards communityCards;
    public int dealer = -1;
    public boolean playerMove = false;
    public long maxRaise = 0L;
    public boolean flopShown;
    public boolean turnShown;
    public boolean riverShown;
    public boolean firstGame;
    public boolean bigBlindAction;
    @FXML
    private Canvas canvasCommunity;

    @FXML
    private Canvas canvasPlayer0;

    @FXML
    private Canvas canvasPlayer1;

    @FXML
    private Canvas canvasPlayer2;

    @FXML
    private Canvas canvasPlayer3;

    @FXML
    private ToggleButton leaveTableButton;

    public List<Canvas> canvasList;

    @FXML
    public void initialize() {
        firstGame = true;
        canvasList = Arrays.asList(canvasPlayer0, canvasPlayer1, canvasPlayer2, canvasPlayer3);
    }

    @FXML
    void handleCall() {
        if (playerMove) {
            call(bets[0], maxBet());
            playerMove = false;
            setPlayerLabel(0);
            bet(1);
        }
    }

    @FXML
    void handleCheck() {
        if (playerMove && (bets[0].getBet() == maxBet())) {
            check(bets[0]);
            playerMove = false;
            setPlayerLabel(0);
            bet(1);
        }
    }

    @FXML
    void handleFold() {
        if (playerMove) {
            fold(bets[0]);
            playerMove = false;
            setPlayerLabel(0);
            bet(1);
        }
    }

    @FXML
    void handleRaise() {
        if (playerMove) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("poker-raise-view.fxml"));
                DialogPane view = loader.load();

                //Set the player into the dialog.
                PokerRaiseController controller = loader.getController();
                controller.setSlider(maxRaise);

                // Create the dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Set Raise");
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setDialogPane(view);

                // Show the dialog and wait until the user closes it
                Optional<ButtonType> clickedButton = dialog.showAndWait();
                if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    raise(bets[0], controller.getRaise());
                    playerMove = false;
                    setPlayerLabel(0);
                    bet(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayersLabels() {
        for (int i = 0; i < NUM_PLAYERS; ++i) {
            setPlayerLabel(i);
        }
    }

    private void setPlayerLabel(int index) {
        GraphicsContext graphicsContext = canvasList.get(index).getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 25, 220, 20);
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.strokeText(players[index].getUsername(), 0, 40);
        graphicsContext.strokeText(Long.toString(players[index].getBalance()), 120, 40);
    }

    public void drawPlayersHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasList.get(i).getGraphicsContext2D();
            Card[] hand = hands[i].getCards();
            Image card1 = getImageCard(hand[0]);
            Image card2 = getImageCard(hand[1]);
            graphicsContext.drawImage(card1, 0, 80, CARD_WIDTH, CARD_HEIGHT);
            graphicsContext.drawImage(card2, 120, 80, CARD_WIDTH, CARD_HEIGHT);
        }
    }

    public Image getImageCard(Card card) {
        String path = String.format("%s/%s.png", IMAGE_DIRECTORY, card.toString());
        try {
            return new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void drawFlop() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card1 = getImageCard(communityCards.getCommunityCardAt(0));
        Image card2 = getImageCard(communityCards.getCommunityCardAt(1));
        Image card3 = getImageCard(communityCards.getCommunityCardAt(2));
        graphicsContext.drawImage(card1, 0, 0, CARD_WIDTH, CARD_HEIGHT);
        graphicsContext.drawImage(card2, 120, 0, CARD_WIDTH, CARD_HEIGHT);
        graphicsContext.drawImage(card3, 240, 0, CARD_WIDTH, CARD_HEIGHT);
    }

    public void drawTurn() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card = getImageCard(communityCards.getCommunityCardAt(communityCards.TURN));
        graphicsContext.drawImage(card, 360, 0, CARD_WIDTH, CARD_HEIGHT);
    }

    public void drawRiver() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card = getImageCard(communityCards.getCommunityCardAt(communityCards.RIVER));
        graphicsContext.drawImage(card, 480, 0, CARD_WIDTH, CARD_HEIGHT);
    }

    public void startGame(Player player) {
        if (leaveTableButton.isSelected())
            returnToLobby();
        else {
            deck = new Deck();
            deck.shuffle();
            pot = new Pot();
            flopShown = false;
            turnShown = false;
            riverShown = false;
            bigBlindAction = false;
            clearCommunityCanvas();
            if (firstGame) {
                setPlayers(player);
                firstGame = false;
            }
            setPlayersLabels();
            setDealer();
            setBets();
            setBlinds();
            setHands();
            drawPlayersHands();
            bet((dealer + 3) % NUM_PLAYERS);
        }
    }

    private void clearCommunityCanvas() {
        communityCards = new CommunityCards();
        canvasCommunity.getGraphicsContext2D().clearRect(0, 0, 580, CARD_HEIGHT);
    }

    public void flop() {
        flopShown = true;
        deck.drawCard();
        Card[] cards = {deck.drawCard(), deck.drawCard(), deck.drawCard()};
        communityCards.setFlop(cards);
        drawFlop();
        bet((dealer + 3) % NUM_PLAYERS);
    }

    public void turn() {
        turnShown = true;
        deck.drawCard();
        communityCards.setTurn(deck.drawCard());
        drawTurn();
        bet((dealer + 3) % NUM_PLAYERS);
    }

    public void river() {
        riverShown = true;
        deck.drawCard();
        communityCards.setRiver(deck.drawCard());
        drawRiver();
        bet((dealer + 3) % NUM_PLAYERS);
    }

    public void setDealer() {
        dealer = (dealer + 1) % NUM_PLAYERS;
        System.out.println("Dealer in posizione " + dealer);
        System.out.println("Piccolo buio in posizione " + (dealer + 1) % NUM_PLAYERS);
        System.out.println("Grande buio in posizione " + (dealer + 2) % NUM_PLAYERS);
    }

    public void setPlayers(Player player) {
        players[0] = player;
        for (int i = 1; i < NUM_PLAYERS; i++)
            players[i] = generateBot(player);
    }

    public Player generateBot(Player player) {
        Player p = new Player();
        p.setBalance(RandomGenerator.getDefault().nextLong((long) (player.getBalance() - (player.getBalance() * 0.5)), (long) (player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", RandomGenerator.getDefault().nextInt(1, 1000)));
        return p;
    }

    public void setBlinds() {
        bets[(dealer + 1) % NUM_PLAYERS].initializeBet();
        bets[(dealer + 2) % NUM_PLAYERS].initializeBet();
        playerBets(bets[(dealer + 1) % NUM_PLAYERS], BLIND / 2);
        playerBets(bets[(dealer + 2) % NUM_PLAYERS], BLIND);
        setPlayerLabel((dealer + 1) % NUM_PLAYERS);
        setPlayerLabel((dealer + 2) % NUM_PLAYERS);
    }

    public void setHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Card[] hand = {deck.drawCard(), deck.drawCard()};
            hands[(dealer + 1 + i) % NUM_PLAYERS] = new PlayerHand(players[(dealer + 1 + i) % NUM_PLAYERS], hand);
        }
    }

    public void setBets() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (i == (dealer + 2) % NUM_PLAYERS)
                bets[i] = new PlayerBet(players[i], true);
            else bets[i] = new PlayerBet(players[i], false);
        }
    }

    public void bet(int index) {
        if (stopBetting()) {
            resetBets();
            if (!flopShown)
                flop();
            else if (!turnShown)
                turn();
            else if (!riverShown)
                river();
            else {
                System.out.println("SHOWDOWN!");
                startGame(players[0]);
            }
        } else if (bets[index].isFolded()) {
            bet((index + 1) % NUM_PLAYERS);
        } else {
            if (index == (dealer + 2) % NUM_PLAYERS && !bigBlindAction)
                bigBlindAction = true;
            bets[index].initializeBet();
            if (index == 0) {
                playerMove = true;
            } else {
                if (bets[index].getBet() < maxBet()) {
                    call(bets[index], maxBet());
                    System.out.println("CALL!");
                } else {
                    System.out.println("CHECK!");
                    check(bets[index]);
                }
                setPlayerLabel(index);
                bet((index + 1) % NUM_PLAYERS);
            }
        }
    }

    private void resetBets() {
        for (PlayerBet playerBet : bets) {
            playerBet.setBet(-1L);
        }
        maxRaise = 0L;
    }

    public void check(PlayerBet player) {
        playerBets(player, 0L);
    }

    public void call(PlayerBet player, long bet) {
        if (player.isBigBlind() && !flopShown)
            bet = Math.max(bet, 2 * BLIND);
        else bet = Math.max(bet, BLIND);
        playerBets(player, bet - player.getBet());
    }

    public void raise(PlayerBet playerBet, long amount) {
        amount = Math.min(playerBet.getPlayer().getBalance(), amount);
        playerBets(playerBet, amount);
        maxRaise = Math.max(amount, maxRaise);
    }

    public void fold(PlayerBet playerBet) {
        playerBet.setFolded(true);
    }

    public void playerBets(PlayerBet playerBet, long bet) {
        playerBet.getPlayer().setBalance(playerBet.getPlayer().getBalance() - bet);
        playerBet.addBet(bet);
        pot.addAmount(bet);
    }

    private boolean stopBetting() {
        int count = 0;
        long bet = 0L;
        boolean equals = true, first = true;

        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (!bets[i].isFolded()) {
                count++;
                if (first) {
                    bet = bets[i].getBet();
                    if (bet < 0)
                        equals = false;
                    first = false;
                } else if (bet != bets[i].getBet())
                    equals = false;
            }
        }
        if (count == 1)
            return true;
        //In the first round of bets the Big Blind can check, raise or fold.
        if (!flopShown && !bigBlindAction)
            return false;
        return equals;
    }

    private long maxBet() {
        long max = 0;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (max < bets[i].getBet())
                max = bets[i].getBet();
        }
        return max;
    }

    public void returnToLobby() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("poker-lobby-view.fxml"));
            Parent root = loader.load();

            //Set the player into the controller.
            PokerLobbyController controller = loader.getController();
            controller.setPlayer(players[0]);

            //Create the stage.
            Stage stage = (Stage) leaveTableButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
