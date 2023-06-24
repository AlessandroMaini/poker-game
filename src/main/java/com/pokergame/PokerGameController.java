package com.pokergame;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

public class PokerGameController {
    final public int cardHeight = 145;
    final public int cardWidth = 100;
    final public static String IMAGE_DIRECTORY = "./src/main/resources/com/pokergame/cards";
    final public int NUM_PLAYERS = 4;
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

    public List<Canvas> canvasList;
    public Deck deck;
    public Player[] players = new Player[NUM_PLAYERS];
    public PlayerHand[] hands = new PlayerHand[NUM_PLAYERS];

    @FXML
    public void initialize() {
        deck = new Deck();
        deck.shuffle();
        canvasList = Arrays.asList(canvasPlayer0, canvasPlayer1, canvasPlayer2, canvasPlayer3);
    }

    public void setPlayers(Player player) {
        players[0] = player;
        for (int i = 1; i < NUM_PLAYERS; i++)
            players[i] = generateBot(player);
        setPlayersLabels();
        setPlayersHands();
        drawPlayersHands();
    }

    public Player generateBot(Player player) {
        Player p = new Player();
        p.setBalance(RandomGenerator.getDefault().nextLong((long)(player.getBalance() - (player.getBalance() * 0.5)),
                (long)(player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", RandomGenerator.getDefault().nextInt(1,1000)));
        return p;
    }

    public void setPlayersLabels() {
        for (int i = 0; i < NUM_PLAYERS; ++i) {
            GraphicsContext graphicsContext = canvasList.get(i).getGraphicsContext2D();
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.setFont(Font.font(14));
            graphicsContext.fillText(players[i].getUsername(), 0, 40);
            graphicsContext.fillText(Long.toString(players[i].getBalance()), 120, 40);
        }
    }

    public void setPlayersHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Card[] hand = {deck.drawCard(), deck.drawCard()};
            hands[i] = new PlayerHand(players[i], hand);
        }
    }

    public void drawPlayersHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            GraphicsContext graphicsContext = canvasList.get(i).getGraphicsContext2D();
            Card[] hand = hands[i].getCards();
            Image card1 = getImageCard(hand[0]);
            Image card2 = getImageCard(hand[1]);
            graphicsContext.drawImage(card1, 0, 80, 100, 145);
            graphicsContext.drawImage(card2, 120, 80, 100, 145);
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
}
