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

/**
 * Game view controller.
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class PokerGameController {
    /** Card images dimensions */
    final public int CARD_HEIGHT = 145;
    final public int CARD_WIDTH = 100;
    /** Path to the card images directory */
    final public static String IMAGE_CARDS_DIRECTORY = "./src/main/resources/com/pokergame/cards";
    /** Path to the dealer image */
    final public String IMAGE_DEALER = "./src/main/resources/com/pokergame/dealer.png";
    /** Path to the chip images directory */
    final public String IMAGE_CHIPS_DIRECTORY = "./src/main/resources/com/pokergame/chips";
    /** Number of players at the table */
    final public int NUM_PLAYERS = 4;
    /** Value of the blind, the minimal bet */
    final public static long BLIND = 20L;
    public Deck deck;
    public Pot pot;
    public Player[] players = new Player[NUM_PLAYERS];
    public PlayerHand[] hands = new PlayerHand[NUM_PLAYERS];
    public PlayerBet[] bets = new PlayerBet[NUM_PLAYERS];
    public CommunityCards communityCards;
    /** Dealer index in the players array */
    public int dealer = -1;
    /** Determine if is the user turn */
    public boolean userMove;
    /** Determine the current maximal raise */
    public long maxRaise = 0L;
    /** Determine if it is the first game at the table, to initialize the bot players */
    public boolean firstGame;
    /** Determine if is the big blind turn */
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

    /**
     * Initialize the control class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        firstGame = true;
        canvasListPlayers = Arrays.asList(canvasPlayer0, canvasPlayer1, canvasPlayer2, canvasPlayer3);
        canvasListDealer = Arrays.asList(canvasDealer0, canvasDealer1, canvasDealer2, canvasDealer3);
        canvasListChips = Arrays.asList(canvasBet0, canvasBet1, canvasBet2, canvasBet3);
    }

    /**
     * Handles the call action by the user.
     */
    @FXML
    void handleCall() {
        if (userMove) {
            call(bets[0], maxBet());
            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            bet(1);
        }
    }

    /**
     * Handles the check action by the user.
     */
    @FXML
    void handleCheck() {
        if (userMove && (bets[0].getBet() == maxBet())) {
            check(bets[0]);
            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            bet(1);
        }
    }

    /**
     * Handles the fold action by the user.
     */
    @FXML
    void handleFold() {
        if (userMove) {
            fold(bets[0]);
            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            bet(1);
        }
    }

    /**
     * Handles the raise action by the user, showing a Dialog to select the amount to raise.
     */
    @FXML
    void handleRaise() {
        if (userMove) {
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
                    userMove = false;
                    setPlayerLabel(0);
                    clearCanvasChoose();
                    bet(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setPlayersLabels() {
        for (int i = 0; i < NUM_PLAYERS; ++i) {
            setPlayerLabel(i);
        }
    }

    /**
     * Shows the player info on the screen.
     *
     * @param index the index of the player in the players array
     */
    void setPlayerLabel(int index) {
        GraphicsContext graphicsContext = canvasListPlayers.get(index).getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 25, 220, 20);
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.strokeText(players[index].getUsername(), 0, 40);
        graphicsContext.strokeText(Long.toString(players[index].getBalance()), 120, 40);
    }

    /**
     * Draws the players hand cards.
     */
    void drawPlayersHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasListPlayers.get(i).getGraphicsContext2D();
            Card[] hand = hands[i].getCards();
            Image card1 = getImageCard(hand[0]);
            Image card2 = getImageCard(hand[1]);
            graphicsContext.drawImage(card1, 0, 80, CARD_WIDTH, CARD_HEIGHT);
            graphicsContext.drawImage(card2, 120, 80, CARD_WIDTH, CARD_HEIGHT);
        }
    }

    /**
     * Obtain the image of a certain card.
     *
     * @param card the card whose image you want
     *
     * @return the card's image
     */
    Image getImageCard(Card card) {
        String path = String.format("%s/%s.png", IMAGE_CARDS_DIRECTORY, card.toString());
        //String path = String.format("%s/back.png", IMAGE_CARDS_DIRECTORY);
        try {
            return new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Draws the flop cards.
     */
    void drawFlop() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card1 = getImageCard(communityCards.getCommunityCardAt(0));
        Image card2 = getImageCard(communityCards.getCommunityCardAt(1));
        Image card3 = getImageCard(communityCards.getCommunityCardAt(2));
        graphicsContext.drawImage(card1, 0, 0, CARD_WIDTH, CARD_HEIGHT);
        graphicsContext.drawImage(card2, 120, 0, CARD_WIDTH, CARD_HEIGHT);
        graphicsContext.drawImage(card3, 240, 0, CARD_WIDTH, CARD_HEIGHT);
    }

    /**
     * Draws the turn card.
     */
    void drawTurn() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card = getImageCard(communityCards.getCommunityCardAt(communityCards.TURN));
        graphicsContext.drawImage(card, 360, 0, CARD_WIDTH, CARD_HEIGHT);
    }

    /**
     * Draws the river card.
     */
    void drawRiver() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card = getImageCard(communityCards.getCommunityCardAt(communityCards.RIVER));
        graphicsContext.drawImage(card, 480, 0, CARD_WIDTH, CARD_HEIGHT);
    }

    /**
     * Draws the dealer icon.
     */
    void drawDealer() {
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

    /**
     * Draws the player bet with chips.
     *
     * @param playerBet the player whose bet you want to draw
     */
    void drawPlayerChips(PlayerBet playerBet) {
        int index = getPlayerBetIndex(playerBet);
        GraphicsContext graphicsContext = canvasListChips.get(index).getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, 220, 120);
        String betString = String.format("Bet: %d", pot.getPlayerAmount(index));
        graphicsContext.setTextAlign(TextAlignment.RIGHT);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.fillText(betString, 220, 10);
        // The number of different types of chips
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

    void drawSingleChip(GraphicsContext graphicsContext, Image image, int num) {
        int y = ((num % 5) * 20) + 20;
        int x = 200 - (num / 5 * 20);
        graphicsContext.drawImage(image, x, y, 20, 20);
    }

    void getPlayerChips(long bet, int[] chips) {
        long[] values = {10, 20, 40, 60, 100, 200, 1000};
        for (int i = values.length - 1; i >= 0; i--) {
            while (bet - values[i] >= 0) {
                bet -= values[i];
                chips[i]++;
            }
        }
    }

    /**
     * Obtain the index of a certain player in the playerBet array.
     *
     * @param playerBet the player whose index you want
     *
     * @return the index
     */
    public int getPlayerBetIndex(PlayerBet playerBet) {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (bets[i].equals(playerBet))
                return i;
        }
        return -1;
    }

    /**
     * Clears the chips shown on the screen.
     */
    void clearChips() {
        for (Canvas canvas : canvasListChips) {
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, 220, 120);
        }
    }

    /**
     * Writes on screen the options for the player.
     */
    void setCanvasChoose() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        if (bets[0].getBet() != maxBet()) {
            String callString = String.format("You have to call %d", maxBet() - bets[0].getBet());
            graphicsContext.fillText(callString, 0, 10);
        }
        graphicsContext.fillText("Choose your action:", 0, 30);
    }

    void clearCanvasChoose() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, 217, 40);
    }

    /**
     * Updates the narrator box.
     *
     * @param text the text to add at the narrator
     */
    void updateTextFlowNarrator(Text text) {
        if (textFlowNarrator.getChildren().size() == 12)
            textFlowNarrator.getChildren().remove(0);
        textFlowNarrator.getChildren().add(text);
    }

    /**
     * Creates a new community cards object and clears the community cards shown on the screen.
     */
    void clearCommunityCanvas() {
        communityCards = new CommunityCards();
        canvasCommunity.getGraphicsContext2D().clearRect(0, 0, 580, CARD_HEIGHT);
    }

    /**
     * Starts a poker game.
     *
     * @param player the user's player
     */
    public void startGame(Player player) {
        if (leaveTableButton.isSelected())
            returnToLobby();
        else {
            deck = new Deck();
            deck.shuffle();
            pot = new Pot(NUM_PLAYERS);
            bigBlindAction = false;
            userMove = false;
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

    /**
     * Generate the flop.
     */
    public void flop() {
        communityCards.setFlopShown(true);
        deck.drawCard();
        Card[] cards = {deck.drawCard(), deck.drawCard(), deck.drawCard()};
        communityCards.setFlop(cards);
        drawFlop();
        bet((dealer + 3) % NUM_PLAYERS);
    }

    /**
     * Generate the turn.
     */
    public void turn() {
        communityCards.setTurnShown(true);
        deck.drawCard();
        communityCards.setTurn(deck.drawCard());
        drawTurn();
        bet((dealer + 3) % NUM_PLAYERS);
    }

    /**
     * Generate the river.
     */
    public void river() {
        communityCards.setRiverShown(true);
        deck.drawCard();
        communityCards.setRiver(deck.drawCard());
        drawRiver();
        bet((dealer + 3) % NUM_PLAYERS);
    }

    /**
     * Decide the dealer position.
     */
    public void setDealer() {
        dealer = (dealer + 1) % NUM_PLAYERS;
        System.out.println("Dealer in posizione " + dealer);
        System.out.println("Piccolo buio in posizione " + (dealer + 1) % NUM_PLAYERS);
        System.out.println("Grande buio in posizione " + (dealer + 2) % NUM_PLAYERS);
    }

    /**
     * Generate the players array, with the user's player and bots.
     *
     * @param player the user's player
     */
    public void setPlayers(Player player) {
        players[0] = player;
        for (int i = 1; i < NUM_PLAYERS; i++)
            players[i] = generateBot(player);
    }

    /**
     * Generate a bot player.
     *
     * @param player the user's player
     *
     * @return the bot player
     */
    public Player generateBot(Player player) {
        Player p = new Player();
        p.setBalance(randomGenerator.nextLong((long) (player.getBalance() - (player.getBalance() * 0.5)), (long) (player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", randomGenerator.nextInt(1, 1000)));
        return p;
    }

    /**
     * Sets the blind bets.
     */
    public void setBlinds() {
        bets[(dealer + 1) % NUM_PLAYERS].initializeBet();
        bets[(dealer + 2) % NUM_PLAYERS].initializeBet();
        playerBets(bets[(dealer + 1) % NUM_PLAYERS], BLIND / 2);
        playerBets(bets[(dealer + 2) % NUM_PLAYERS], BLIND);
        setPlayerLabel((dealer + 1) % NUM_PLAYERS);
        setPlayerLabel((dealer + 2) % NUM_PLAYERS);
    }

    /**
     * Generate the player hands.
     */
    public void setHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Card[] hand = {deck.drawCard(), deck.drawCard()};
            hands[(dealer + 1 + i) % NUM_PLAYERS] = new PlayerHand(players[(dealer + 1 + i) % NUM_PLAYERS], hand);
        }
    }

    /**
     * Initialize the playerBet objects array.
     */
    public void setBets() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (i == (dealer + 2) % NUM_PLAYERS)
                bets[i] = new PlayerBet(players[i], true);
            else
                bets[i] = new PlayerBet(players[i], false);
        }
    }

    /**
     * Determine the player action in the current turn.
     *
     * @param index the index of the player
     */
    public void bet(int index) {
        if (stopBetting()) {
            resetBets();
            if (!communityCards.isFlopShown())
                flop();
            else if (!communityCards.isTurnShown())
                turn();
            else if (!communityCards.isRiverShown())
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
                userMove = true;
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

    /**
     * Resets the playerBet objects array for a new turn.
     */
    public void resetBets() {
        for (PlayerBet playerBet : bets) {
            playerBet.setBet(-1L);
        }
        maxRaise = 0L;
    }

    /**
     * Execute the check action.
     *
     * @param playerBet the player who checks
     */
    public void check(PlayerBet playerBet) {
        playerBets(playerBet, 0L);
        getActionText("CHECK", playerBet.getPlayer().getUsername(), 0);
    }

    /**
     * Execute the call action.
     *
     * @param playerBet the player who calls
     * @param bet the bet to call
     */
    public void call(PlayerBet playerBet, long bet) {
        if (playerBet.isBigBlind() && !communityCards.isFlopShown())
            bet = Math.max(bet, 2 * BLIND);
        else
            bet = Math.max(bet, BLIND);
        playerBets(playerBet, bet - playerBet.getBet());
        getActionText("CALL", playerBet.getPlayer().getUsername(), 0);
    }

    /**
     * Execute the raise action.
     *
     * @param playerBet the player who raises
     * @param amount the amount to raise
     */
    public void raise(PlayerBet playerBet, long amount) {
        maxRaise = Math.max(amount, maxRaise);
        amount = Math.min(playerBet.getPlayer().getBalance(), amount + (maxBet() - playerBet.getBet()));
        playerBets(playerBet, amount);
        getActionText("RAISE", playerBet.getPlayer().getUsername(), maxBet());
    }

    /**
     * Execute the fold action.
     *
     * @param playerBet the player who folds
     */
    public void fold(PlayerBet playerBet) {
        playerBet.setFolded(true);
        getActionText("FOLD", playerBet.getPlayer().getUsername(), 0);
    }

    /**
     * Generate the text describing the action.
     *
     * @param action the action of the player
     * @param user the username of the player
     * @param amount the optional amount of the raise, otherwise 0
     */
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

    /**
     * Handle the player's bet
     *
     * @param playerBet the player who bets
     * @param bet the amount of the bet
     */
    public void playerBets(PlayerBet playerBet, long bet) {
        playerBet.getPlayer().setBalance(playerBet.getPlayer().getBalance() - bet);
        playerBet.addBet(bet);
        pot.addAmount(bet);
        pot.addPlayerAmount(getPlayerBetIndex(playerBet), bet);
        drawPlayerChips(playerBet);
    }

    /**
     * Determine if the bet cycle is ended for the current turn.
     *
     * @return true if the cycle have to stop, otherwise false
     */
    public boolean stopBetting() {
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
        if (!communityCards.isFlopShown() && !bigBlindAction)
            return false;
        return equals;
    }

    /**
     * Determine the maximal bet in the current turn.
     *
     * @return the maximal bet
     */
    public long maxBet() {
        long max = 0;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (max < bets[i].getBet())
                max = bets[i].getBet();
        }
        return max;
    }

    /**
     * Switches the Scene from Game to Lobby, if you decide to leave the table.
     */
    void returnToLobby() {
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
