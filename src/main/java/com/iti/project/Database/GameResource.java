package com.iti.project.Database;

import org.json.simple.JSONObject;

public class GameResource {

    private int id;
    private String playerOne;
    private String playerTwo;
    private String board;
    private String state;
    private int playerOneSave;
    private int playerTwoSave;

    public GameResource(int id, String playerOne, String playerTwo,
                        String board, String state, int playerOneSave, int playerTwoSave) {
        this.id = id;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = board;
        this.state = state;
        this.playerOneSave = playerOneSave;
        this.playerTwoSave = playerTwoSave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPlayerOneSave() {
        return playerOneSave;
    }

    public void setPlayerOneSave(int playerOneSave) {
        this.playerOneSave = playerOneSave;
    }

    public int getPlayerTwoSave() {
        return playerTwoSave;
    }

    public void setPlayerTwoSave(int playerTwoSave) {
        this.playerTwoSave = playerTwoSave;
    }

    public JSONObject toJson(){
        JSONObject gameJson = new JSONObject();
        gameJson.put("id", this.id);
        gameJson.put("playerOne", this.playerOne);
        gameJson.put("playerTwo", this.playerTwo);
        //gameJson.put("board", this.board);
        //gameJson.put("state", this.state);
        return gameJson;
    }
}
