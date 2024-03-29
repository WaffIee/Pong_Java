package com.pongame.database;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DbConnection {
    public static Connection connection;

    private DbConnection() {
        initializeDatabase(connection);
        // Singleton Principle: Private constructor to prevent external instantiation
    }

    public static Connection getInstance() {
        if (connection == null) {
            synchronized (DbConnection.class) {
                if (connection == null) {
                    Properties props = new Properties();
                    try {
                        props.load(new FileInputStream("config.properties"));
                        String dbUrl = props.getProperty("db.url");
                        String dbUser = props.getProperty("db.user");
                        String dbPassword = props.getProperty("db.password");
                        Class.forName("org.h2.Driver");
                        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                        System.out.println("Connection Success");
                        initializeDatabase(connection);
                    } catch (IOException | SQLException | ClassNotFoundException ex) {
                        System.err.println("Exception occurred");
                        ex.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection Closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Connection con = DbConnection.getInstance();
        DbConnection.closeConnection();
    }

    private static void initializeDatabase(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Read and execute the create_database.sql file
            InputStream is = DbConnection.class.getClassLoader().getResourceAsStream("create_database.sql");
            if (is == null) {
                throw new RuntimeException("Failed to find the file ");
            }
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(sql);
            System.out.println("H2 Database Initialized");
        } catch (IOException | SQLException e) {
            System.err.println("Failed to initialize H2 database");
            e.printStackTrace();
        }
    }

}
