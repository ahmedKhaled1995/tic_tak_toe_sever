package com.iti.project;

public class GameResource {

    private int id;
    private String playerOne;
    private String playerTwo;
    private String board;
    private String state;

    public GameResource(int id, String playerOne, String playerTwo, String board, String state) {
        this.id = id;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = board;
        this.state = state;
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
}
