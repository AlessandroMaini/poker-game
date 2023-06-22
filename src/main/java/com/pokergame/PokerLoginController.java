package com.pokergame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class PokerLoginController {
    /** path to the players database */
    final public static String playerDatabase = "./src/main/resources/com/pokergame/players.json";
    @FXML
    private TextField username;
    @FXML
    private Button loginButton;
    Player player = new Player();

    /**
     * Initialize the control class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        username.textProperty().addListener(((observable, oldValue, newValue) -> player.setUsername(newValue)));
    }

    /**
     * Returns the players saved in the database
     *
     * @return the list of Player objects
     */
    static List<Player> getPlayerData() {
        try (FileReader file = new FileReader(playerDatabase)) {
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
        boolean found = false;
        List<Player> players = getPlayerData();
        if (players != null)
            for (Player p : players) {
                if (p.username.equals(player.getUsername())) {
                    player = p;
                    found = true;
                    break;
                }
            }
        if (!found)
            new Alert(Alert.AlertType.ERROR, "Could not find the user").showAndWait();
        else {
            try {
                switchToSceneLobby();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            List<Player> players = getPlayerData();
            if (players != null)
                for (Player p : players) {
                    if (p.username.equals(player.getUsername())) {
                        new Alert(Alert.AlertType.ERROR, "This username is already taken").showAndWait();
                        found = true;
                        break;
                    }
                }
            if (!found) {
                player = new Player(player.getUsername());
                try {
                    switchToSceneLobby();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    /**
     * Switches the Scene from Login to the Lobby
     *
     * @throws IOException if there's no loader
     */
    public void switchToSceneLobby() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("poker-lobby-view.fxml"));
        Parent root = loader.load();

        //Set the player into the controller.
        PokerLobbyController controller = loader.getController();
        controller.setPlayer(player);

        //Create the stage.
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

