package com.pokergame;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PokerStatisticsController {

    @FXML
    private TableView<Player> playerTable;

    @FXML
    private TableColumn<Player, Long> balanceColumn;

    @FXML
    private TableColumn<Player, String> usernameColumn;

    @FXML
    public void initialize() {
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        playerTable.setItems(FXCollections.observableList(PokerLoginController.getPlayerData()));
    }
}
