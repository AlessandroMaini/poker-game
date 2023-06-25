package com.pokergame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokerLobbyController {

    @FXML
    private Label balanceLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private MenuBar menuBar;
    Player player;

    @FXML
    public void initialize() {
        balanceLabel.textProperty().addListener((observable, oldValue, newValue) -> player.setBalance(Long.parseLong(newValue)));
        usernameLabel.textProperty().addListener((observable, oldValue, newValue) -> player.setUsername(newValue));
    }

    @FXML
    public void handlePlay() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("poker-game-view.fxml"));
            Parent root = loader.load();

            //Set the player into the controller.
            PokerGameController controller = loader.getController();
            controller.startGame(player);

            //Create the stage.
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleChangePlayer() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("poker-login-view.fxml"));
            Parent root = loader.load();

            //Create the stage.
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExit() {
        System.exit(0);
    }

    /**
     * Sets the player selected in the login scene.
     * @param player the player selected
     */
    public void setPlayer(Player player) {
        this.player = player;
        balanceLabel.setText(Long.toString(player.getBalance()));
        usernameLabel.setText(player.getUsername());
        updatePlayerDatabase();
    }

    /**
     * Updates the players database with the info about the current player.
     */
    public void updatePlayerDatabase() {
        List<Player> players = PokerLoginController.getPlayerData();
        boolean found = false;
        if (players != null)
            for (Player p : players) {
                if (p.getUsername().equals(player.getUsername())) {
                    p.setBalance(player.getBalance());
                    found = true;
                }
            }
        if (!found) {
            if (players == null)
                players = new ArrayList<>();
            players.add(player);
        }
        try (FileWriter file = new FileWriter(PokerLoginController.PLAYER_DATABASE)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, players);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not save data").showAndWait();
        }
    }
}
