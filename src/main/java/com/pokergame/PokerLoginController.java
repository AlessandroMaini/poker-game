package com.pokergame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class PokerLoginController {
    /** path to the players database */
    public final String PLAYERS_DATABASE = "./src/main/resources/com/pokergame/players.json";
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
    List<Player> getPlayerData() {
        try (FileReader file = new FileReader(PLAYERS_DATABASE)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(file, new TypeReference<>() {
            });
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
        List<Player> players = getPlayerData();
        boolean found = false;
        for (Player p : players) {
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
        if (player.getUsername() == null || player.getUsername().contains(" "))
            new Alert(Alert.AlertType.ERROR, "Invalid name (no spaces)").showAndWait();
        else {
            List<Player> players = getPlayerData();
            boolean found = false;
            for (Player p : players) {
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

