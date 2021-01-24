package com.iti.project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDao {

    private final DatabaseManager databaseManager;

    public GameDao(){
        this.databaseManager = new DatabaseManager();
    }

    public List<GameResource> getPlayerGames(String playerName){
        Connection connection = this.databaseManager.getDatabaseConnection();
        List<GameResource> players = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM Game WHERE player_one = ? OR player_two = ?");
            statement.setString(1, playerName);
            statement.setString(2, playerName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                players.add( new GameResource(
                                resultSet.getInt("id"),
                                resultSet.getString("player_one"),
                                resultSet.getString("player_two"),
                                resultSet.getString("board"),
                                resultSet.getString("state")
                        )
                );
            }
            connection.close();
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int addGame(GameResource game){
        Connection connection = this.databaseManager.getDatabaseConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Game (player_one, player_two, board, state)" +
                            " VALUES (?, ?, ?, ?)"
            );
            statement.setString(1, game.getPlayerOne());
            statement.setString(2, game.getPlayerTwo());
            statement.setString(3, game.getBoard());
            statement.setString(4, game.getState());
            int res = statement.executeUpdate();
            connection.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
