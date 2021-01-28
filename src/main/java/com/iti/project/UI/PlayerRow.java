package com.iti.project.UI;

public class PlayerRow {

    private String userName;
    private int score;
    private String status;

    public PlayerRow(String userName, int score, String status) {
        this.userName = userName;
        this.score = score;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
