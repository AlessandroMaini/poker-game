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
import javafx.scene.text.*;
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
    final public static String IMAGE_CARDS_DIRECTORY = "./src/main/resources/com/pokergame/cards";
    final public String IMAGE_DEALER = "./src/main/resources/com/pokergame/dealer.png";
    final public String IMAGE_CHIPS_DIRECTORY = "./src/main/resources/com/pokergame/chips";
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
    public RandomGenerator randomGenerator = RandomGenerator.getDefault();
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

    @FXML
    private Canvas canvasBet0;

    @FXML
    private Canvas canvasBet1;

    @FXML
    private Canvas canvasBet2;

    @FXML
    private Canvas canvasBet3;

    @FXML
    private Canvas canvasDealer0;

    @FXML
    private Canvas canvasDealer1;

    @FXML
    private Canvas canvasDealer2;

    @FXML
    private Canvas canvasDealer3;

    @FXML
    private Canvas canvasChoose;

    @FXML
    private TextFlow textFlowNarrator;

    public List<Canvas> canvasListPlayers;
    public List<Canvas> canvasListDealer;
    public List<Canvas> canvasListChips;

    @FXML
    public void initialize() {
        firstGame = true;
        canvasListPlayers = Arrays.asList(canvasPlayer0, canvasPlayer1, canvasPlayer2, canvasPlayer3);
        canvasListDealer = Arrays.asList(canvasDealer0, canvasDealer1, canvasDealer2, canvasDealer3);
        canvasListChips = Arrays.asList(canvasBet0, canvasBet1, canvasBet2, canvasBet3);
    }

    @FXML
    void handleCall() {
        if (playerMove) {
            call(bets[0], maxBet());
            playerMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            bet(1);
        }
    }

    @FXML
    void handleCheck() {
        if (playerMove && (bets[0].getBet() == maxBet())) {
            check(bets[0]);
            playerMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            bet(1);
        }
    }

    @FXML
    void handleFold() {
        if (playerMove) {
            fold(bets[0]);
            playerMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
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
                    clearCanvasChoose();
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
        GraphicsContext graphicsContext = canvasListPlayers.get(index).getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 25, 220, 20);
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.strokeText(players[index].getUsername(), 0, 40);
        graphicsContext.strokeText(Long.toString(players[index].getBalance()), 120, 40);
    }

    public void drawPlayersHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasListPlayers.get(i).getGraphicsContext2D();
            Card[] hand = hands[i].getCards();
            Image card1 = getImageCard(hand[0]);
            Image card2 = getImageCard(hand[1]);
            graphicsContext.drawImage(card1, 0, 80, CARD_WIDTH, CARD_HEIGHT);
            graphicsContext.drawImage(card2, 120, 80, CARD_WIDTH, CARD_HEIGHT);
        }
    }

    public Image getImageCard(Card card) {
        String path = String.format("%s/%s.png", IMAGE_CARDS_DIRECTORY, card.toString());
        //String path = String.format("%s/back.png", IMAGE_CARDS_DIRECTORY);
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

    public void drawDealer() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasListDealer.get(i).getGraphicsContext2D();
            if (i == dealer) {
                try {
                    graphicsContext.drawImage(new Image(new FileInputStream(IMAGE_DEALER)), 0, 0, 75, 75);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else
                graphicsContext.clearRect(0, 0, 75, 75);
        }
    }

    public void drawPlayerChips(PlayerBet playerBet) {
        int index = getPlayerBetIndex(playerBet);
        GraphicsContext graphicsContext = canvasListChips.get(index).getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, 220, 120);
        String betString = String.format("Bet: %d", pot.getPlayerAmount(index));
        graphicsContext.setTextAlign(TextAlignment.RIGHT);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.fillText(betString, 220, 10);
        final int CHIP_COLORS = 7;
        int[] chips = new int[CHIP_COLORS];
        Arrays.fill(chips, 0);
        getPlayerChips(pot.getPlayerAmount(index), chips);
        int chipCounter = 0;
        for (int i = 0; i < CHIP_COLORS; i++) {
            String path = String.format("%s/%d-chip.png", IMAGE_CHIPS_DIRECTORY, i);
            Image chipImage = null;
            try {
                chipImage = new Image(new FileInputStream(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (chips[i] > 0 && chipCounter < 55) {
                drawSingleChip(graphicsContext, chipImage, chipCounter);
                chipCounter++;
                chips[i]--;
            }
        }
    }

    private void drawSingleChip(GraphicsContext graphicsContext, Image image, int num) {
        int y = ((num % 5) * 20) + 20;
        int x = 200 - (num / 5 * 20);
        graphicsContext.drawImage(image, x, y, 20, 20);
    }

    private void getPlayerChips(long bet, int[] chips) {
        long[] values = {10, 20, 40, 60, 100, 200, 1000};
        for (int i = values.length - 1; i >= 0; i--) {
            while (bet - values[i] >= 0) {
                bet -= values[i];
                chips[i]++;
            }
        }
    }

    private int getPlayerBetIndex(PlayerBet playerBet) {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (bets[i].equals(playerBet))
                return i;
        }
        return -1;
    }

    public void clearChips() {
        for (Canvas canvas : canvasListChips) {
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, 220, 120);
        }
    }

    public void setCanvasChoose() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        if (bets[0].getBet() != maxBet()) {
            String callString = String.format("You have to call %d", maxBet() - bets[0].getBet());
            graphicsContext.fillText(callString, 0, 10);
        }
        graphicsContext.fillText("Choose your action:", 0, 30);
    }

    private void clearCanvasChoose() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, 217, 40);
    }

    public void updateTextFlowNarrator(Text text) {
        if (textFlowNarrator.getChildren().size() == 12)
            textFlowNarrator.getChildren().remove(0);
        textFlowNarrator.getChildren().add(text);
    }

    public void startGame(Player player) {
        if (leaveTableButton.isSelected())
            returnToLobby();
        else {
            deck = new Deck();
            deck.shuffle();
            pot = new Pot(NUM_PLAYERS);
            flopShown = false;
            turnShown = false;
            riverShown = false;
            bigBlindAction = false;
            clearCommunityCanvas();
            clearChips();
            if (firstGame) {
                setPlayers(player);
                firstGame = false;
            }
            setPlayersLabels();
            setDealer();
            drawDealer();
            Text startText = new Text("-----NEW GAME STARTING-----\n");
            startText.setFill(Color.BLACK);
            startText.setFont(Font.font("System", FontWeight.BOLD, 14));
            updateTextFlowNarrator(startText);
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
        p.setBalance(randomGenerator.nextLong((long) (player.getBalance() - (player.getBalance() * 0.5)), (long) (player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", randomGenerator.nextInt(1, 1000)));
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
            else
                bets[i] = new PlayerBet(players[i], false);
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
                setCanvasChoose();
                playerMove = true;
            } else {
                if (bets[index].getBet() < maxBet()) {
                    if (randomGenerator.nextInt() % 2 == 0) {
                        call(bets[index], maxBet());
                        System.out.println("CALL!");
                    } else {
                        raise(bets[index], Math.max(maxRaise, BLIND));
                        System.out.println("RAISE!");
                    }
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

    public void check(PlayerBet playerBet) {
        playerBets(playerBet, 0L);
        getActionText("CHECK", playerBet.getPlayer().getUsername(), 0);
    }

    public void call(PlayerBet playerBet, long bet) {
        if (playerBet.isBigBlind() && !flopShown)
            bet = Math.max(bet, 2 * BLIND);
        else
            bet = Math.max(bet, BLIND);
        playerBets(playerBet, bet - playerBet.getBet());
        getActionText("CALL", playerBet.getPlayer().getUsername(), 0);
    }

    public void raise(PlayerBet playerBet, long amount) {
        maxRaise = Math.max(amount, maxRaise);
        amount = Math.min(playerBet.getPlayer().getBalance(), amount + (maxBet() - playerBet.getBet()));
        playerBets(playerBet, amount);
        getActionText("RAISE", playerBet.getPlayer().getUsername(), maxBet());
    }

    public void fold(PlayerBet playerBet) {
        playerBet.setFolded(true);
        getActionText("FOLD", playerBet.getPlayer().getUsername(), 0);
    }

    public void getActionText(String action, String user, long amount) {
        String actionString;
        if (amount != 0) {
            actionString = String.format("%s %sS to %d\n", user, action, amount);
        } else
            actionString = String.format("%s %sS\n", user, action);
        Text actionText = new Text(actionString);
        actionText.setFont(Font.font(14));
        switch (action) {
            case "RAISE" -> actionText.setFill(Color.BLUE);
            case "CALL" -> actionText.setFill(Color.GREEN);
            case "CHECK" -> actionText.setFill(Color.GREY);
            case "FOLD" -> actionText.setFill(Color.RED);
        }
        updateTextFlowNarrator(actionText);
    }

    public void playerBets(PlayerBet playerBet, long bet) {
        playerBet.getPlayer().setBalance(playerBet.getPlayer().getBalance() - bet);
        playerBet.addBet(bet);
        pot.addAmount(bet);
        pot.addPlayerAmount(getPlayerBetIndex(playerBet), bet);
        drawPlayerChips(playerBet);
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
