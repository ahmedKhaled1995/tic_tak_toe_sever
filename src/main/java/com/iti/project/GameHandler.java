package com.iti.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GameHandler {

    private static Logger logger = LoggerFactory.getLogger(GameHandler.class);

    private static final HashMap<Integer, Game> GAME_MAP = new HashMap<>();
    // contains the user and and json object containing the gameId as well as the opponent name
    // Json object has two keys 'gameId' and 'opponentName'
    private static final HashMap<String, JSONObject> USERS_IN_GAME = new HashMap<>();
    private static final HashMap<String, GameHandler> NAME_SOCKET_MAP = new HashMap<>();
    private static final PlayerController PLAYER_CONTROLLER =  new PlayerController();

    private Socket currentSocket;
    private Player player;
    private DataInputStream dis;
    private PrintStream ps;

    public GameHandler(Socket cs)  {
        try {
            this.currentSocket = cs;
            this.player = null; // Will get initialized when the user successfully login
            this.dis = new DataInputStream(cs.getInputStream());
            this.ps= new PrintStream(cs.getOutputStream());

            Thread listenToClientThread = new Thread(() -> {
                this.listenToClient();
            });
            listenToClientThread.start();

        } catch (IOException e) {
            e.printStackTrace();
            //closeConnection();
        }
    }

    /** Used to close connection with the client */
    private void closeConnection(){
        try {
            this.currentSocket.close();
            this.dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ps.close();
        if(this.player != null){  // player whi left was logged in
            NAME_SOCKET_MAP.remove(this.player.getUserName());
            logger.info("Closed Connection user name: " + this.player.getUserName());
        }else{
            logger.info("Closed Connection user who is not logged in");
        }
        logger.info("Connection count before closing connection: " + NAME_SOCKET_MAP.size());
        logger.info("Connection count after closing connection: " + NAME_SOCKET_MAP.size());
        handleClientLeaving();
    }

    /** This method is the ears of the sever, it's a method running in its own
     thread, all it does is listed to client requests */
    private void listenToClient(){
        boolean clientStillConnected = true;
        while(clientStillConnected) {
            String str= null;
            try {
                str = dis.readLine();
                handleClientReply(str);
            } catch (IOException e) {
                // Handle exception here if a client exits (generated by str = dis.readLine())
                //e.printStackTrace();
                clientStillConnected = false;
                closeConnection();
            }
        }
    }

    /** Used to send a message to all connected clients */
    private void broadCast(String msg) {
        for (Map.Entry<String,GameHandler> entry : NAME_SOCKET_MAP.entrySet()){
            entry.getValue().ps.println(msg);
        }
    }

    /** This method is heart of the program, it takes to client requests (reply)
     and reply according to the 'type' received is the request json object */
    private void handleClientReply(String reply){
        JSONObject replyJson = parseStringToJsonObject(reply);
        String type = replyJson.get("type").toString();
        if(type.equals("gameTurn")){
            int gameId = Integer.parseInt(replyJson.get("gameId").toString());
            int index = Integer.parseInt(replyJson.get("index").toString());
            int symbol = Integer.parseInt(replyJson.get("symbol").toString());
            handleNextTurn(gameId, index, symbol);
        }else if(type.equals("login")){
            String name = replyJson.get("userName").toString();
            String password = replyJson.get("password").toString();
            handleLogin(name, password);
        }else if(type.equals("getUsers")){
            handleSendAllUsers();
        }else if(type.equals("tryGameWithOpponent")){
            String possibleOpponentName = replyJson.get("opponent").toString();
            if(NAME_SOCKET_MAP.get(possibleOpponentName) == null){  // opponent is not online
                handleGameRejection(possibleOpponentName + " is currently not online!", null);
            }else if(USERS_IN_GAME.get(possibleOpponentName) == null){  // opponent is free to play
                askOpponentForGame(possibleOpponentName);
            }else{    // opponent is busy (in other game)
                handleGameRejection("Opponents is in another game!", null);
            }
        }else if(type.equals("startGameResponse")){
            String gameAccepted = replyJson.get("result").toString();
            String opponentName = replyJson.get("opponent").toString();
            if(gameAccepted.equals("true")){
                handleGameStart(opponentName);
            }else{
                handleGameRejection(this.player.getUserName() + " declined the game!", opponentName);
            }
        }
    }

    /** Handles next turn in the multi player game,
     index is the place on the board that was clicked (0->8)
     symbol is either 1 (for 'X') or -1 (for 'O') */
    private void handleNextTurn(int gameId, int index, int symbol){
        // Getting game info
        Game game = GAME_MAP.get(gameId);
        GameHandler possibleWinner = NAME_SOCKET_MAP.get(game.nextTurn(index, symbol));
        GameHandler playerToPlay = NAME_SOCKET_MAP.get(game.getPlayerToPlay());
        GameHandler playerToWait = NAME_SOCKET_MAP.get(game.getPlayerToWait());
        // Creating the json objects to send to the players (two client)
        JSONObject playerToPlayJson = new JSONObject();
        playerToPlayJson.put("type", "gameTurnResult");
        playerToPlayJson.put("won", "false");
        playerToPlayJson.put("lost", "false");
        playerToPlayJson.put("tie", "false");
        playerToPlayJson.put("myTurn", "true");
        playerToPlayJson.put("index", index);
        JSONObject playerToWaitJson = new JSONObject(playerToPlayJson);
        playerToWaitJson.replace("myTurn", "false");
        // Checking if game is still running, tie or game over (someone has won)
        if(possibleWinner == null && game.getGameTurns() < 9){  // Game still running
            playerToPlay.ps.println(playerToPlayJson.toJSONString());
            playerToWait.ps.println(playerToWaitJson.toJSONString());
        }else if(possibleWinner == null && game.getGameTurns() >= 9){  // Tie
            playerToPlayJson.replace("tie", "true");
            playerToWaitJson.replace("tie", "true");
            playerToPlay.ps.println(playerToPlayJson.toJSONString());
            playerToWait.ps.println(playerToWaitJson.toJSONString());
            removeGame(game);
        }else if(possibleWinner != null){  // Some one has won
            GameHandler winner = NAME_SOCKET_MAP.get(game.getWinner());
            GameHandler loser = NAME_SOCKET_MAP.get(game.getLoser());
            JSONObject winnerJson = null;
            JSONObject loserJson = null;
            if(winner.equals(playerToPlay)){
                winnerJson = playerToPlayJson;
                loserJson = playerToWaitJson;
            }else{
                winnerJson = playerToWaitJson;
                loserJson = playerToPlayJson;
            }
            winnerJson.replace("won", "true");
            loserJson.replace("lost", "true");
            winner.ps.println(winnerJson.toJSONString());
            loser.ps.println(loserJson.toJSONString());
            removeGame(game);
        }
    }

    /** Used to send all users to the client when the client logs in */
    private void handleSendAllUsers(){
        JSONObject sendToClient = new JSONObject();
        JSONArray allUsers = new JSONArray();
        for(Player player : PLAYER_CONTROLLER.getPlayersData()){
            // Checking if user is online
            for (Map.Entry<String,GameHandler> entry : NAME_SOCKET_MAP.entrySet()){
                if(player.getUserName().equals(entry.getKey())){
                    player.setStatus("Online");
                }
            }
            allUsers.add(player.toJson());
        }
        sendToClient.put("type", "usersList");
        sendToClient.put("users", allUsers);
        this.ps.println(sendToClient.toJSONString());
    }

    /** Used to notify other clients when a new client connects */
    private void signalOnlineUser(Player loggedInUser){
        JSONObject sendToClient = new JSONObject();
        sendToClient.put("type", "newLoggedInUser");
        sendToClient.put("loggedInUser", loggedInUser.toJson());
        broadCast(sendToClient.toJSONString());
    }

    /** Used to notify other clients when a client leaves */
    private void signalUserLogout(Player loggedOutUser){
        JSONObject sendToClient = new JSONObject();
        sendToClient.put("type", "loggedOutUser");
        sendToClient.put("loggedOutUser", this.player.toJson());
        broadCast(sendToClient.toJSONString());
    }

    /** Used to notify the client attempting to login if the login was successful or not,
     also it signals the other clients the a new user has joined */
    private void handleLogin(String userName, String password){
        JSONObject object = new JSONObject();
        boolean success = true;
        Player player = PLAYER_CONTROLLER.getPlayer(userName, password);
        if(player == null){  // login failed
            success = false;
        }else{
            this.player = player;
            NAME_SOCKET_MAP.put(this.player.getUserName(), this);
        }
        object.put("type", "loginResult");
        object.put("success", success);
        object.put("userName", player.getUserName());
        object.put("score", this.player.getScore());
        // Notifying other clients a new player has joined
        signalOnlineUser(this.player.getPlayerToSendToClient());
        logger.info("{} has logged in", this.player.getUserName());
        // Sending response to the user
        this.ps.println(object.toJSONString());
    }

    /** Connections are already closed and user is removed from nameSocketMap when this methods is called,
     All we handle is check if the client that left is in game or not
     and if he is in game, we terminate the game and notify the other player */
    private void handleClientLeaving(){
        if(this.player != null){  // meaning user who left was logged in
            JSONObject gameInfo = USERS_IN_GAME.get(this.player.getUserName());
            if(gameInfo != null){  // That means that the client left was in a game
                int gameId = Integer.parseInt(gameInfo.get("gameId").toString());
                String opponentName = gameInfo.get("opponentName").toString();
                // Now, we notify the other client that the game has been terminated
                JSONObject sendToOtherClient = new JSONObject();
                sendToOtherClient.put("type", "gameTerminated");
                NAME_SOCKET_MAP.get(opponentName).ps.println(sendToOtherClient.toJSONString());
                removeGame(GAME_MAP.get(gameId));
            }
            // Here, I am notifying other clients that client has left (to update the listview in the frontend)
            signalUserLogout(this.player);
        }
    }

    /** Used to remove a game when it's finished */
    private void removeGame(Game game){
        logger.info("Removed game with id {}", game.getGameId());
        GAME_MAP.remove(game.getGameId());
        USERS_IN_GAME.remove(game.getPlayerOne());
        USERS_IN_GAME.remove(game.getPlayerTwo());
    }

    /** Handles game rejection, note if opponent is null, that means game was rejected
     by the server because the user was either busy or offline. If opponent name is
     provided, that means that this opponent rejected the game */
    private void handleGameRejection(String error, String opponent){
        if(opponent != null){ // Means game was rejected because user declined
            // Note that the opponent String in the method argument is the player who requested the game
            GameHandler userWhoRequestedGame = NAME_SOCKET_MAP.get(opponent);
            JSONObject sendToClient = new JSONObject();
            sendToClient.put("type", "gameRejected");
            sendToClient.put("error", error);
            userWhoRequestedGame.ps.println(sendToClient.toJSONString());
        }else{   // Means user was rejected because opponent is busy (in another game) or offline
            JSONObject sendToClient = new JSONObject();
            sendToClient.put("type", "gameRejected");
            sendToClient.put("error", error);
            this.ps.println(sendToClient.toJSONString());
        }
    }

    /** Used to notify the client that some one wants to play with him*/
    private void askOpponentForGame(String opponentName) {
        GameHandler opponentClient = NAME_SOCKET_MAP.get(opponentName);
        JSONObject sendToOpponent = new JSONObject();
        sendToOpponent.put("type", "startGameRequest");
        sendToOpponent.put("opponentName", this.player.getUserName());
        opponentClient.ps.println(sendToOpponent.toJSONString());
    }

    /** Handles creation of the game between two players and stores the necessary information
     Note that here 'this' refers to the player who ASKED for the game,
     while 'opponent' is the person who accepted the game */
    private void handleGameStart(String opponent){
        // Creating game info
        int gameId = GAME_MAP.size();  // gameId starts at zero
        // Storing game info
        JSONObject playerOneGameInfo = new JSONObject();
        playerOneGameInfo.put("gameId", gameId);
        playerOneGameInfo.put("opponentName", opponent);
        JSONObject playerTwoGameInfo = new JSONObject();
        playerTwoGameInfo.put("gameId", gameId);
        playerTwoGameInfo.put("opponentName", this.player.getUserName());
        USERS_IN_GAME.put(this.player.getUserName(), playerOneGameInfo);
        USERS_IN_GAME.put(opponent, playerTwoGameInfo);
        // Starting game and adding it to games list
        Game newGame = new Game(gameId, this.player.getUserName(), opponent);
        logger.info("Started game with id {}", gameId);
        GAME_MAP.put(gameId ,newGame);
        // Sending to both players game start info
        JSONObject sendToPlayerOne = new JSONObject();
        JSONObject sendToPlayerTwo = new JSONObject();
        sendToPlayerOne.put("type", "startGame");
        sendToPlayerOne.put("gameId", gameId);
        sendToPlayerOne.put("opponent", opponent);
        sendToPlayerOne.put("myTurn", true);
        sendToPlayerTwo.put("type", "startGame");
        sendToPlayerTwo.put("gameId", gameId);
        sendToPlayerTwo.put("opponent", this.player.getUserName());
        sendToPlayerTwo.put("myTurn", false);
        NAME_SOCKET_MAP.get(newGame.getPlayerOne()).ps.println(sendToPlayerOne.toJSONString());
        NAME_SOCKET_MAP.get(newGame.getPlayerTwo()).ps.println(sendToPlayerTwo.toJSONString());
    }

    public boolean equals(GameHandler other){
        return this.currentSocket.equals(other.currentSocket);
    }

    private JSONObject parseStringToJsonObject(String jsonString){
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}