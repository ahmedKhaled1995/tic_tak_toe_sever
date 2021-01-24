package com.iti.project;

import org.json.simple.JSONObject;

public class Game {

    private final int gameId;
    private final String playerOne;
    private final String playerTwo;
    private String winner;
    private String loser;
    private boolean playerOneTurn;  // At the start of the game, 'playerOne' always starts
    private boolean hasSomeOneWon;
    private int gameTurns;   // Max is 9, if reached and no one has won, then it's a tie


    private final int[] board;  // game board
    private final JSONObject gameValues;

    public Game(int gameId, String playerOne, String playerTwo){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new int[]{0,0,0,0,0,0,0,0,0};  // button id is array index, 'X' or 'O' is the array element
        this.gameValues = new JSONObject();
        this.playerOneTurn = true;  // 'playerOne' is 'X', 'playerTwo' is 'O'
        this.hasSomeOneWon = false;
        this.gameTurns = 0;
        this.winner = null;
        this.loser = null;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public String getPlayerToPlay(){
        if(this.playerOneTurn){
            return playerOne;
        }else{
            return playerTwo;
        }
    }

    public String getPlayerToWait(){
        if(this.playerOneTurn){
            return playerTwo;
        }else{
            return playerOne;
        }
    }

    public int getGameTurns(){
        return this.gameTurns;
    }

    public String getWinner(){
        return this.winner;
    }

    public String getLoser(){
        return this.loser;
    }

    public int getGameId(){
        return this.gameId;
    }

    public String nextTurn(int index, int symbol){   // ex: [index, element] ex: [0->8, 1 or -1]
        /*https://stackoverflow.com/questions/24371957/iterate-through-jsonobject-from-root-in-json-simple*/
        this.playerOneTurn = !this.playerOneTurn;
        JSONObject turnJson = new JSONObject();
        turnJson.put(index, symbol);
        this.gameValues.put(gameTurns, turnJson);
        this.gameTurns++;
        this.board[index] = symbol;
        return this.checkGameOver();
    }

    private String checkGameOver(){
        // First, we check if win condition is reached
        int winner = this.checkWin();  // 0 no one won, 1 playerOne has won, -1 playerTwo has won
        if(this.checkTie()){
            return null;  // Tie
        }else if (winner != 0){  // Won condition has been reached
            if(winner == 1){
                this.winner = playerOne;
                this.loser = playerTwo;
                return playerOne;
            }else{
                this.winner = playerTwo;
                this.loser = playerOne;
                return playerTwo;
            }
        }
        return null;
    }

    /*
    0 1 2
    3 4 5
    6 7 8
    */
    // Returns 0 if no one won, 1 if 'X' won and -1 if 'O' won
    private int checkWin(){
        // Checking horizontally
        if(board[0] != 0 && (board[0] == board[1] && board[0] == board[2])){
            this.hasSomeOneWon = true;
            return board[0];
        }else if(board[3] != 0 && (board[3] == board[4] && board[3] == board[5])){
            this.hasSomeOneWon = true;
            return board[3];
        }else if(board[6] != 0 && (board[6] == board[7] && board[6] == board[8])){
            this.hasSomeOneWon = true;
            return board[6];
        }
        // Checking vertically
        else if(board[0] != 0 && (board[0] == board[3] && board[0] == board[6])){
            this.hasSomeOneWon = true;
            return board[0];
        }else if(board[1] != 0 && (board[1] == board[4] && board[1] == board[7])){
            this.hasSomeOneWon = true;
            return board[1];
        }else if(board[2] != 0 && (board[2] == board[5] && board[2] == board[8])){
            this.hasSomeOneWon = true;
            return board[2];
        }
        // Checking diagonally
        else if(board[0] != 0 && (board[0] == board[4] && board[0] == board[8])){
            this.hasSomeOneWon = true;
            return board[0];
        }else if(board[2] != 0 && (board[2] == board[4] && board[2] == board[6])){
            this.hasSomeOneWon = true;
            return board[2];
        }
        return 0;
    }

    private boolean checkTie(){
        return !this.hasSomeOneWon && this.gameTurns >= 9;
    }

   /* public void resetGame(){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new int[]{0,0,0,0,0,0,0,0,0};  // button id is array index, 'X' or 'O' is the array element
        this.orderOfPlay = new int[]{0,0,0,0,0,0,0,0,0};
        this.playerOneTurn = true;  // 'playerOne' is 'X', 'playerTwo' is 'O'
        this.hasSomeOneWon = false;
        this.gameTurns = 0;
        this.winner = null;
        this.loser = null;
    }*/

    public JSONObject getGameBoard(){
        return this.gameValues;
    }

    public JSONObject getStatus(){
        JSONObject status = new JSONObject();
        if(this.gameTurns >= 9 || this.hasSomeOneWon){
            status.put("gameComplete", "true");
            if(hasSomeOneWon){
                status.put("winner", this.winner);
                status.put("loser", this.loser);
                status.put("tie", "false");
            }else{
                status.put("tie", "true");
            }
        }else{
            status.put("gameComplete", "false");
        }
        return status;
    }
}
