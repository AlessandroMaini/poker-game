package com.pokergame;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

public class PokerGameController {
    final public static String IMAGE_DIRECTORY = "./src/main/resources/com/pokergame/cards";
    final public int NUM_PLAYERS = 4;

    @FXML
    private AnchorPane gamePane;

    @FXML
    private HBox playerHBox;

    @FXML
    private HBox communityHBox;

    @FXML
    private Label playerBalanceLabel;

    @FXML
    private Label playerUsernameLabel;

    @FXML
    private Label bot1BalanceLabel;

    @FXML
    private Label bot1UsernameLabel;

    @FXML
    private Label bot2BalanceLabel;

    @FXML
    private Label bot2UsernameLabel;

    @FXML
    private Label bot3BalanceLabel;

    @FXML
    private Label bot3UsernameLabel;


    Deck deck;
    /** The real player is at index 0, the others are bots. */
    Player[] players = new Player[NUM_PLAYERS];

    @FXML
    public void initialize() {
        deck = new Deck();
        deck.shuffle();
        setPlayersImages();
        playerBalanceLabel.textProperty().addListener((observable, oldValue, newValue) -> players[0].setBalance(Long.parseLong(newValue)));
        playerUsernameLabel.textProperty().addListener((observable, oldValue, newValue) -> players[0].setUsername(newValue));
        bot1UsernameLabel.textProperty().addListener((observable, oldValue, newValue) -> players[1].setUsername(newValue));
        bot1BalanceLabel.textProperty().addListener((observable, oldValue, newValue) -> players[1].setBalance(Long.parseLong(newValue)));
        bot2UsernameLabel.textProperty().addListener((observable, oldValue, newValue) -> players[2].setUsername(newValue));
        bot2BalanceLabel.textProperty().addListener((observable, oldValue, newValue) -> players[2].setBalance(Long.parseLong(newValue)));
        bot3UsernameLabel.textProperty().addListener((observable, oldValue, newValue) -> players[3].setUsername(newValue));
        bot3BalanceLabel.textProperty().addListener((observable, oldValue, newValue) -> players[3].setBalance(Long.parseLong(newValue)));
    }

    public void setPlayers(Player player) {
        players[0] = player;
        playerBalanceLabel.setText(Long.toString(player.getBalance()));
        playerUsernameLabel.setText(player.getUsername());
        players[1] = generateBot(player);
        bot1BalanceLabel.setText(Long.toString(players[1].getBalance()));
        bot1UsernameLabel.setText(players[1].getUsername());
        players[2] = generateBot(player);
        bot2BalanceLabel.setText(Long.toString(players[2].getBalance()));
        bot2UsernameLabel.setText(players[2].getUsername());
        players[3] = generateBot(player);
        bot3BalanceLabel.setText(Long.toString(players[3].getBalance()));
        bot3UsernameLabel.setText(players[3].getUsername());
    }

    public Player generateBot(Player player) {
        Player p = new Player();
        p.setBalance(RandomGenerator.getDefault().nextLong((long)(player.getBalance() - (player.getBalance() * 0.5)),
                        (long)(player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", RandomGenerator.getDefault().nextInt()));
        return p;
    }

    public List<HBox> playersHBoxes() {
        return gamePane.getChildren().stream().filter(child -> child instanceof HBox && child != communityHBox).map(child -> (HBox) child).collect(Collectors.toList());
    }

    public void setPlayersImages() {
        for (HBox hBox : playersHBoxes()) {
            hBox.getChildren().stream().filter(child -> child instanceof ImageView).map(child -> (ImageView) child).forEach(this::addImage);
        }
    }

    public void addImage(ImageView imageView) {
        imageView.setImage(getImageCard(deck.drawCard()));
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
