package com.pokergame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PokerLoginController {
    @FXML
    private TextField username;
    Player player = new Player();

    /**
     * Initialize the control class. This method is automatically called after the fxml file has been loaded.
     */
    public void initialize() {
        username.textProperty().addListener(((observable, oldValue, newValue) -> player.setUsername(newValue)));
    }

    /**
     * Returns the players saved in the database
     *
     * @return the list of Player objects
     */
    static List<Player> getPlayerData() {
        try (FileReader file = new FileReader("./src/main/resources/com/pokergame/players.json")) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Allows you to log in the game as an existing player
     */
    @FXML
    private void handleLogin() {
        boolean found = false;
        for (Player p : Objects.requireNonNull(getPlayerData())) {
            if (p.username.equals(player.getUsername())) {
                new Alert(Alert.AlertType.INFORMATION, "Successful access").showAndWait();
                found = true;
                break;
            }
        }
        if (!found)
            new Alert(Alert.AlertType.ERROR, "Could not find the user").showAndWait();
    }

    /**
     * Allows you to sign in the game as a new player
     */
    @FXML
    private void handleSignIn() {
        if (player.getUsername() == null || player.getUsername().contains(" ") || player.getUsername().length() == 0)
            new Alert(Alert.AlertType.ERROR, "Invalid name (no spaces)").showAndWait();
        else {
            boolean found = false;
            for (Player p : Objects.requireNonNull(getPlayerData())) {
                if (p.username.equals(player.getUsername())) {
                    new Alert(Alert.AlertType.ERROR, "This username is already taken").showAndWait();
                    found = true;
                    break;
                }
            }
            if (!found)
                new Alert(Alert.AlertType.INFORMATION, "Successful access").showAndWait();
        }
    }

    /**
     * Shows the statistics of all the registered users in a Dialog
     */
    @FXML
    private void handleShowStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("poker-statistics-view.fxml"));
            DialogPane view = loader.load();

            // Create the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Poker Statistics");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            // Show the dialog and wait until the user closes it
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Address Application");
        alert.setHeaderText("About");
        alert.setContentText("Authors: Alessandro Maini and Matteo Guidetti");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

