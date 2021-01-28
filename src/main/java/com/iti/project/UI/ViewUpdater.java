package com.iti.project.UI;

import com.iti.project.EntryPoint;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Optional;

public class ViewUpdater {

    private TableView<PlayerRow> playersList;
    private TextArea consoleOutputArea;

    public ViewUpdater(TableView<PlayerRow> playersList, TextArea consoleOutputArea){
        this.playersList = playersList;
        this.consoleOutputArea = consoleOutputArea;
        this.initializeTable();
    }

    private void initializeTable(){
        TableColumn<PlayerRow, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(350);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<PlayerRow, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setMinWidth(150);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        TableColumn<PlayerRow, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setMinWidth(200);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.playersList.getColumns().addAll(nameColumn, scoreColumn, statusColumn);
    }

    public void fillTableWithData(){
        ObservableList<PlayerRow> playersTable = FXCollections.observableArrayList();
        JSONArray players = EntryPoint.getGameServer().getUsers();
        for(Object player : players){
            JSONObject playerResource = (JSONObject) player;
            playersTable.add(
                    new PlayerRow(
                            playerResource.get("userName").toString(),
                            Integer.parseInt(playerResource.get("score").toString()),
                            playerResource.get("status").toString()
                    )
            );
        }
        this.playersList.setItems(playersTable);
    }

    public void resetTable(){
        this.playersList.getItems().clear();
    }

    public void refreshTable(){
        this.resetTable();
        this.fillTableWithData();
    }

    public void updateLoggedInUser(String userName){
        Optional<PlayerRow> playerRow = this.playersList.getItems().stream().
                filter(item -> item.getUserName().equals(userName)).findAny();
        playerRow.get().setStatus("Online");
        this.playersList.refresh();
    }

    public void updateLoggedOutUser(String userName){
        Optional<PlayerRow> playerRow = this.playersList.getItems().stream().
                filter(item -> item.getUserName().equals(userName)).findAny();
        playerRow.get().setStatus("Offline");
        this.playersList.refresh();
    }

    public void updateSignedUpUser(String userName){
        this.playersList.getItems().add(new PlayerRow(userName, 0, "Online"));
    }

    public void updateConsole(String log){
        Platform.runLater(()->{
            this.consoleOutputArea.appendText(log + "\n");
        });
    }

    public void resetConsole(){
        Platform.runLater(()->{
            this.consoleOutputArea.setText("");
        });
    }
}
