package com.pongame.dao;

import com.pongame.classes.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {
    private Connection connection;

    public PlayerDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createPlayer(Player player) {
        String sql = "INSERT INTO Player (name, birthday, password) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, player.getBirthday()); // Set as a string
            preparedStatement.setString(3, player.getPassword());

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Player authenticatePlayer(String name, String password) {
        String sql = "SELECT * FROM Player WHERE name = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String playerName = resultSet.getString("name");
                String playerBirthday = resultSet.getString("birthday");
                String playerPassword = resultSet.getString("password");

                Player player = new Player( playerName, playerBirthday, playerPassword);
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
