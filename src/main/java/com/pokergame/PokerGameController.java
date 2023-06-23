package com.pokergame;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class PokerGameController {
    final public static String imageDirectory = "./src/main/resources/com/pokergame/cards";

    @FXML
    private AnchorPane gamePane;

    @FXML
    private HBox playerHBox;

    Deck deck;

    @FXML
    public void initialize() {
        deck = new Deck();
        deck.shuffle();
        setImages();
    }

    public List<HBox> hBoxes() {
        return gamePane.getChildren().stream().filter(child -> child instanceof HBox).map(child -> (HBox) child).collect(Collectors.toList());
    }

    public void setImages() {
        for (HBox hBox : hBoxes()) {
            hBox.getChildren().stream().filter(child -> child instanceof ImageView).map(child -> (ImageView) child).forEach(this::addImage);
        }
    }

    public void addImage(ImageView imageView) {
        imageView.setImage(getImageCard(deck.drawCard()));
    }

    public Image getImageCard(Card card) {
        String path = String.format("%s/%s.png", imageDirectory, card.toString());
        System.out.println(path);
        try {
            return new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
