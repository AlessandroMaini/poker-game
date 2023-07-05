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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Game view controller.
 *
 * @author Alessandro Maini
 * @version 2023.07.02
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
    /** Determine if it is the first game at the table, to initialize the bot players */
    public boolean firstGame;
    /** Determine if is the user turn */
    public boolean userMove;
    /** Determine if the game is waiting for the user to click on the next arrow */
    public boolean waitingForNext;
    /** Index of the next player to bet */
    public int nextBetIndex;
    /** Determine if a new game have to start */
    public boolean startNewGame;
    /** Manages the running of the game */
    public GameLogic game;
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
    private VBox textVBoxNarrator;

    public List<Canvas> canvasListPlayers;
    public List<Canvas> canvasListDealer;
    public List<Canvas> canvasListChips;

    /**
     * Initialize the control class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        firstGame = true;
        waitingForNext = false;
        canvasListPlayers = Arrays.asList(canvasPlayer0, canvasPlayer1, canvasPlayer2, canvasPlayer3);
        canvasListDealer = Arrays.asList(canvasDealer0, canvasDealer1, canvasDealer2, canvasDealer3);
        canvasListChips = Arrays.asList(canvasBet0, canvasBet1, canvasBet2, canvasBet3);
    }

    /**
     * Handles the click of the next arrow.
     */
    @FXML
    void handleNext() {
        if (waitingForNext) {
            waitingForNext = false;
            if (startNewGame)
                startGame(game.getPlayerAt(0));
            else
                bet(nextBetIndex);
        }
    }

    /**
     * Handles the call action by the user.
     */
    @FXML
    void handleCall() {
        if (userMove) {
            getActionText(game.call(game.getBetAt(0), game.maxBet()), game.getPlayerAt(0), game.maxBet());
            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            waitingForNext = true;
            nextBetIndex = 1;
        }
    }

    /**
     * Handles the check action by the user.
     */
    @FXML
    void handleCheck() {
        if (userMove && (game.getBetAt(0).getBet() == game.maxBet())) {
            getActionText(game.check(game.getBetAt(0)), game.getPlayerAt(0), game.maxBet());
            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            waitingForNext = true;
            nextBetIndex = 1;
        }
    }

    /**
     * Handles the fold action by the user.
     */
    @FXML
    void handleFold() {
        if (userMove) {
            getActionText(game.fold(game.getBetAt(0)), game.getPlayerAt(0), game.maxBet());
            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            waitingForNext = true;
            nextBetIndex = 1;
        }
    }

    /**
     * Handles the raise action by the user, showing a Dialog to select the amount to raise.
     */
    @FXML
    void handleRaise() {
        if (userMove && !game.isBrokePlayer() && game.getPlayerAt(0).getBalance() > (game.maxBet() - game.getBetAt(0).getBet())) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("poker-raise-view.fxml"));
                DialogPane view = loader.load();

                //Set the player into the dialog.
                PokerRaiseController controller = loader.getController();
                controller.setSlider(game.getMaxRaise());

                // Create the dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Set Raise");
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setDialogPane(view);

                // Show the dialog and wait until the user closes it
                Optional<ButtonType> clickedButton = dialog.showAndWait();
                if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    getActionText(game.raise(game.getBetAt(0), controller.getRaise()), game.getPlayerAt(0), game.maxBet());
                    userMove = false;
                    setPlayerLabel(0);
                    clearCanvasChoose();
                    waitingForNext = true;
                    nextBetIndex = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the all in action by the user.
     */
    @FXML
    void handleAllIn() {
        if (userMove && (!game.isBrokePlayer() || game.getPlayerAt(0).getBalance() <= (game.maxBet() - game.getBetAt(0).getBet()))) {
            getActionText(game.allIn(game.getBetAt(0)), game.getPlayerAt(0), game.maxBet());

            userMove = false;
            setPlayerLabel(0);
            clearCanvasChoose();
            waitingForNext = true;
            nextBetIndex = 1;
        }
    }

    void setPlayersLabels() {
        for (int i = 0; i < GameLogic.NUM_PLAYERS; ++i) {
            setPlayerLabel(i);
        }
    }

    /**
     * Shows the player info on the screen.
     *
     * @param index the index of the player in the players list
     */
    void setPlayerLabel(int index) {
        GraphicsContext graphicsContext = canvasListPlayers.get(index).getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 25, 220, 20);
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.strokeText(game.getPlayerAt(index).getUsername(), 0, 40);
        graphicsContext.strokeText(Long.toString(game.getPlayerAt(index).getBalance()), 120, 40);
    }

    /**
     * Draws the players hand cards.
     */
    void drawPlayersHands(boolean showdown) {
        for (int i = 0; i < GameLogic.NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasListPlayers.get(i).getGraphicsContext2D();
            List<Card> hand = game.getHandAt(i).getCards();
            Image card1 = getImageCard(hand.get(0), i == 0 || showdown, game.getBetAt(i).isFolded());
            Image card2 = getImageCard(hand.get(1), i == 0 || showdown, game.getBetAt(i).isFolded());
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
    Image getImageCard(Card card, boolean show, boolean fold) {
        String path;
        if (fold)
            path = String.format("%s/back-fold.png", IMAGE_CARDS_DIRECTORY);
        else if (!show)
            path = String.format("%s/back.png", IMAGE_CARDS_DIRECTORY);
        else
            path = String.format("%s/%s.png", IMAGE_CARDS_DIRECTORY, card.toString());
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
        Image card1 = getImageCard(game.getCommunityCards().getCommunityCardAt(0), true, false);
        Image card2 = getImageCard(game.getCommunityCards().getCommunityCardAt(1), true, false);
        Image card3 = getImageCard(game.getCommunityCards().getCommunityCardAt(2), true, false);
        graphicsContext.drawImage(card1, 0, 0, CARD_WIDTH, CARD_HEIGHT);
        graphicsContext.drawImage(card2, 120, 0, CARD_WIDTH, CARD_HEIGHT);
        graphicsContext.drawImage(card3, 240, 0, CARD_WIDTH, CARD_HEIGHT);
        setCanvasBestHand();
        getPhaseText("FLOP");
    }

    /**
     * Draws the turn card.
     */
    void drawTurn() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card = getImageCard(game.getCommunityCards().getCommunityCardAt(CommunityCards.TURN), true, false);
        graphicsContext.drawImage(card, 360, 0, CARD_WIDTH, CARD_HEIGHT);
        clearCanvasBestHand();
        setCanvasBestHand();
        getPhaseText("TURN");
    }

    /**
     * Draws the river card.
     */
    void drawRiver() {
        GraphicsContext graphicsContext = canvasCommunity.getGraphicsContext2D();
        Image card = getImageCard(game.getCommunityCards().getCommunityCardAt(CommunityCards.RIVER), true, false);
        graphicsContext.drawImage(card, 480, 0, CARD_WIDTH, CARD_HEIGHT);
        clearCanvasBestHand();
        setCanvasBestHand();
        getPhaseText("RIVER");
    }

    /**
     * Draws the dealer icon.
     */
    void drawDealer() {
        for (int i = 0; i < GameLogic.NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasListDealer.get(i).getGraphicsContext2D();
            if (i == game.getDealer()) {
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
     * @param player the player whose bet you want to draw
     */
    void drawPlayerChips(Player player) {
        int index = game.getPlayers().indexOf(player);
        GraphicsContext graphicsContext = canvasListChips.get(index).getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, 220, 120);
        String betString = String.format("Bet: %d", game.getPot().getPlayerAmount(index));
        graphicsContext.setTextAlign(TextAlignment.RIGHT);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        graphicsContext.fillText(betString, 220, 10);
        // The number of different types of chips
        final int CHIP_COLORS = 9;
        int[] chips = new int[CHIP_COLORS];
        Arrays.fill(chips, 0);
        getPlayerChips(game.getPot().getPlayerAmount(index), chips);
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
        long[] values = {1, 10, 20, 40, 60, 100, 200, 1000, 10000};
        for (int i = values.length - 1; i >= 0; i--) {
            while (bet - values[i] >= 0) {
                bet -= values[i];
                chips[i]++;
            }
        }
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
     * Writes on screen the options for the user.
     */
    void setCanvasChoose() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font(14));
        if (game.getBetAt(0).getBet() != game.maxBet()) {
            String callString = String.format("You have to call %d", game.maxBet() - game.getBetAt(0).getBet());
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
        if (textVBoxNarrator.getChildren().size() == 12)
            textVBoxNarrator.getChildren().remove(0);
        textVBoxNarrator.getChildren().add(text);
    }

    /**
     * Creates a new community cards object and clears the community cards shown on the screen.
     */
    void clearCommunityCanvas() {
        canvasCommunity.getGraphicsContext2D().clearRect(0, 0, 580, CARD_HEIGHT);
    }

    /**
     * Writes on screen the best hand of the user.
     */
    void setCanvasBestHand() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("System", FontWeight.BOLD, 14));
        Hand hand = new Hand(game.getAllPlayerCards(game.getHandAt(0)));
        String userBestHand = String.format("You have %s", getHandString(hand.getBestHand()));
        graphicsContext.fillText(userBestHand, 0, 100);
    }

    void clearCanvasBestHand() {
        GraphicsContext graphicsContext = canvasChoose.getGraphicsContext2D();
        graphicsContext.clearRect(0, 40, 217, 120);
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
            if (firstGame) {
                game = new GameLogic(player);
                firstGame = false;
            }
            userMove = false;
            clearCommunityCanvas();
            clearCanvasBestHand();
            clearChips();
            if (game.initialize()) {
                setPlayersLabels();
                drawDealer();
                drawPlayerChips(game.getPlayerAt((game.getDealer() + 1) % GameLogic.NUM_PLAYERS));
                drawPlayerChips(game.getPlayerAt((game.getDealer() + 2) % GameLogic.NUM_PLAYERS));
                //If one of the two players can't pay for the blind
                if (game.isBrokePlayer())
                    game.handleSidePots(game.getBetAt((game.getDealer() + 2) % GameLogic.NUM_PLAYERS).getBet());
                getPhaseText("-----NEW GAME STARTING-----");
                drawPlayersHands(false);
                nextBetIndex = (game.getDealer() + 3) % GameLogic.NUM_PLAYERS;
                startNewGame = false;
                waitingForNext = true;
            } else
                returnToLobby();
        }
    }

    /**
     * Determine the player action in the current turn.
     *
     * @param index the index of the player
     */
    public void bet(int index) {
        if (game.stopBetting()) {
            if (!game.isBrokePlayer())
                game.resetBets();
            waitingForNext = true;
            nextBetIndex = (game.getDealer() + 1) % GameLogic.NUM_PLAYERS;
            if (game.getCommunityCards().isNotFlopShown()) {
                game.flop();
                drawFlop();
            } else if (!game.getCommunityCards().isTurnShown()) {
                game.turn();
                drawTurn();
            } else if (!game.getCommunityCards().isRiverShown()) {
                game.river();
                drawRiver();
            } else {
                showdown();
                startNewGame = true;
            }
        } else if (game.getBetAt(index).isFolded()) {
            bet((index + 1) % GameLogic.NUM_PLAYERS);
        } else {
            game.initBet(index);
            if (index == 0) {
                setCanvasChoose();
                userMove = true;
            } else {
                getActionText(game.botBet(index), game.getPlayerAt(index), game.maxBet());
                setPlayerLabel(index);
                waitingForNext = true;
                nextBetIndex = (index + 1) % GameLogic.NUM_PLAYERS;
            }
        }
    }

    /**
     * Shows the players hands, determines the score for each hand and decides the winners for each pot.
     */
    void showdown() {
        getPhaseText("SHOWDOWN");
        List<Integer> points = game.getHandsScores();
        drawPlayersHands(true);
        for (int i = 0; i < GameLogic.NUM_PLAYERS; i++)
            if (points.get(i) != 0)
                getShowdownText(points.get(i), game.getPlayerAt(i));
        getPotWinnersText(points, game.getPot());
        for (Pot s : game.getSidePots())
            getPotWinnersText(points, s);
    }

    /**
     * Shows the winning text for each winner of the pot.
     *
     * @param points list of players hand scores
     * @param pot the pot considered
     */
    void getPotWinnersText(List<Integer> points, Pot pot) {
        List<Player> winners = game.getPotWinners(points, pot);
        winners.forEach(winner -> getVictoryText(winner, pot, winners.size()));
    }

    /**
     * Generate the text describing the action, also updates the graphics in case of fold or bet.
     *
     * @param action the action of the player
     * @param player the player
     * @param amount the optional amount of the raise, otherwise 0
     */
    void getActionText(String action, Player player, long amount) {
        String actionString;
        if (action.equals("FOLD"))
            drawPlayersHands(false);
        else
            drawPlayerChips(player);
        if (action.equals("RAISE"))
            actionString = String.format("%s %sS to %d", player.getUsername(), action, amount);
        else if (action.equals("ALL IN"))
            actionString = String.format("%s %s", player.getUsername(), action);
        else
            actionString = String.format("%s %sS", player.getUsername(), action);
        Text actionText = new Text(actionString);
        actionText.setFont(Font.font(14));
        switch (action) {
            case "RAISE" -> actionText.setFill(Color.BLUE);
            case "CALL" -> actionText.setFill(Color.GREEN);
            case "CHECK" -> actionText.setFill(Color.GREY);
            case "FOLD" -> actionText.setFill(Color.RED);
            case "ALL IN" -> actionText.setFill(Color.PURPLE);
        }
        updateTextFlowNarrator(actionText);
    }

    /**
     * Generate the text describing the phase of the game.
     *
     * @param phase the phase of the game
     */
    void getPhaseText(String phase) {
        Text phaseText = new Text(phase);
        phaseText.setFont(Font.font("System", FontWeight.BOLD, 14));
        phaseText.setFill(Color.BLACK);
        updateTextFlowNarrator(phaseText);
    }

    /**
     * Generate the text describing the hand of the player.
     *
     * @param points the score of the player's hand
     * @param p the player
     */
    void getShowdownText(int points, Player p) {
        String showdownString;
        showdownString = String.format("%s has %s", p.getUsername(), getHandString(points));
        Text showdownText = new Text(showdownString);
        showdownText.setFont(Font.font(14));
        showdownText.setFill(Color.BLACK);
        updateTextFlowNarrator(showdownText);
    }

    String getHandString(int points) {
        return switch (points / 1000000) {
            case 1 -> "ONE PAIR";
            case 2 -> "TWO PAIRS";
            case 3 -> "THREE OF A KIND";
            case 4 -> "STRAIGHT";
            case 5 -> "FLUSH";
            case 6 -> "FULL HOUSE";
            case 7 -> "FOUR OF A KIND";
            case 8 -> "STRAIGHT FLUSH";
            default -> "HIGH CARD";
        };
    }

    /**
     * Generate the text for the winning players
     *
     * @param p the winning player
     * @param pot the pot won by the player
     * @param numWinners the number of winners for the pot
     */
    void getVictoryText(Player p, Pot pot, int numWinners) {
        String victoryString;
        victoryString = String.format("%s WINS %d!!", p.getUsername(), (pot.getAmount() / numWinners));
        Text victoryText = new Text(victoryString);
        victoryText.setFill(Color.GREEN);
        victoryText.setFont(Font.font("System", FontWeight.BOLD, 14));
        updateTextFlowNarrator(victoryText);
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
            controller.setPlayer(game.getPlayerAt(0));

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
