package com.iti.project.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDao {

    public List<GameResource> getPlayerGames(String playerName){
        Connection connection = DatabaseManager.getConnection();
        List<GameResource> players = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM Game WHERE player_one = ? AND player_one_save = 1 " +
                    "UNION SELECT * FROM Game WHERE player_two = ? AND player_two_save = 1 ");
            statement.setString(1, playerName);
            statement.setString(2, playerName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                players.add( new GameResource(
                                resultSet.getInt("id"),
                                resultSet.getString("player_one"),
                                resultSet.getString("player_two"),
                                resultSet.getString("board"),
                                resultSet.getString("state"),
                                resultSet.getInt("player_one_save"),
                                resultSet.getInt("player_two_save")
                        )
                );
            }
            //connection.close();
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int addGame(GameResource game){
        Connection connection = DatabaseManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Game (player_one, player_two, board, state, player_one_save, player_two_save)" +
                            " VALUES (?, ?, ?, ?, ?, ?)"
            );
            statement.setString(1, game.getPlayerOne());
            statement.setString(2, game.getPlayerTwo());
            statement.setString(3, game.getBoard());
            statement.setString(4, game.getState());
            statement.setInt(5, game.getPlayerOneSave());
            statement.setInt(6, game.getPlayerTwoSave());
            int res = statement.executeUpdate();
            //connection.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public GameResource getSavedGame(int savedGameId){
        Connection connection = DatabaseManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM Game WHERE id = ?");
            statement.setInt(1, savedGameId);
            ResultSet resultSet = statement.executeQuery();
            return new GameResource( // Note that id is unique so resultSet has only one entry
                    resultSet.getInt("id"),
                    resultSet.getString("player_one"),
                    resultSet.getString("player_two"),
                    resultSet.getString("board"),
                    resultSet.getString("state"),
                    resultSet.getInt("player_one_save"),
                    resultSet.getInt("player_two_save")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
