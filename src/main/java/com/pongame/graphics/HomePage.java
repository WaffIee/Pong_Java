package com.pongame.graphics;

import com.pongame.classes.Player;
import com.pongame.config.DifficultyLevel;
import com.pongame.database.DbConnection;
import com.pongame.game.Game;
import com.pongame.game.PlayWithAI;
import com.pongame.interfaces.IGameDAO;
import com.pongame.interfaces.IPlayerDAO;
import com.pongame.dao.PlayerDAO;
import javax.swing.*;
import java.awt.*;


public class HomePage extends JFrame {
    public JComboBox<DifficultyLevel> difficultyComboBox;
    public final JButton startSinglePlayerButton;
    public final JButton startMultiPlayerButton;
    public final JButton createAccountButton;
    public final JButton loginButton;
    public final JButton profileButton;
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int ICON_WIDTH = 30;
    private static final int ICON_HEIGHT = 30;
    public Game gameInstance ;
    private IPlayerDAO playerDAO;
    private IGameDAO gameDAO;



    public HomePage(Player player) {
        this.playerDAO = new PlayerDAO(DbConnection.getInstance());

        // Background image
        ImageIcon backgroundImage = new ImageIcon("src/main/java/com/pongame/pictures/back.png");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new OverlayLayout(backgroundLabel));
        // Set window properties
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Difficulty combo box
        difficultyComboBox = new JComboBox<>(DifficultyLevel.values());
        difficultyComboBox.setPreferredSize(new Dimension(150, 30));
        // Button size
        Dimension buttonSize = new Dimension(150, 30);
        // Icons for buttons
        ImageIcon singlePlayerIcon = resizeIcon(new ImageIcon("src/main/java/com/pongame/pictures/single_player_icon.png"), ICON_WIDTH, ICON_HEIGHT);
        ImageIcon multiPlayerIcon = resizeIcon(new ImageIcon("src/main/java/com/pongame/pictures/multi_player_icon.png"), ICON_WIDTH, ICON_HEIGHT);
        ImageIcon createAccountIcon = resizeIcon(new ImageIcon("src/main/java/com/pongame/pictures/create_account_icon.png"), ICON_WIDTH, ICON_HEIGHT);
        ImageIcon loginIcon = resizeIcon(new ImageIcon("src/main/java/com/pongame/pictures/login_icon.png"), ICON_WIDTH, ICON_HEIGHT);
        ImageIcon profileIcon = resizeIcon(new ImageIcon("src/main/java/com/pongame/pictures/profile_icon.png"), ICON_WIDTH, ICON_HEIGHT);
        // Buttons with icons
        startSinglePlayerButton = new JButton("Play with Computer", singlePlayerIcon);
        startSinglePlayerButton.setPreferredSize(buttonSize);
        startMultiPlayerButton = new JButton("Start Multiplayer Game", multiPlayerIcon);
        startMultiPlayerButton.setPreferredSize(buttonSize);
        createAccountButton = new JButton("Create Account", createAccountIcon);
        loginButton = new JButton("Login", loginIcon);
        profileButton =  new JButton("Profile", profileIcon);
        // Panel for center components
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 200;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 0, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(new JLabel("Select Difficulty:"), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(difficultyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++; // Increment for the next row
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(startMultiPlayerButton, gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(startSinglePlayerButton, gbc);
        // Panel for top components
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(profileButton);
        profileButton.setVisible(player != null);
        // Panel for bottom components
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(createAccountButton);
        bottomPanel.add(loginButton);
        if (player != null) {
            createAccountButton.setVisible(false);
            loginButton.setVisible(false);
        }
        // Action listeners for buttons
        createAccountButton.addActionListener(e -> {
            PlayerAccountForm accountForm = new PlayerAccountForm();
            accountForm.setVisible(true);
        });
        loginButton.addActionListener(e -> {

            LoginForm loginForm = new LoginForm(playerDAO);
            loginForm.setVisible(true);
        });
        profileButton.addActionListener(e -> {
            UserProfileForm userProfileForm = new UserProfileForm(player);
            userProfileForm.setVisible(true);
            System.out.println("Profile " + player.getName() + ", Birthday: " + player.getBirthday());
        });
        // Add components to background label
        backgroundLabel.add(topPanel, BorderLayout.NORTH);
        backgroundLabel.add(centerPanel, BorderLayout.CENTER);
        backgroundLabel.add(bottomPanel, BorderLayout.SOUTH);
        // Add background label to content pane
        getContentPane().add(backgroundLabel);
        // Game start action listeners
        startMultiPlayerButton.addActionListener(e -> startGame(false, player));
        startSinglePlayerButton.addActionListener(e -> startGame(true, player));
    }


    public void startGame(boolean isSinglePlayer, Player player) {
        DifficultyLevel selectedDifficulty = (DifficultyLevel) difficultyComboBox.getSelectedItem();

        if (isSinglePlayer) {
            gameInstance = new PlayWithAI(selectedDifficulty, player,gameDAO);
        } else {
            gameInstance = new Game(selectedDifficulty, isSinglePlayer, player,gameDAO);
        }

        JFrame gameFrame = new JFrame("Pong Game");
        gameFrame.setSize(WIDTH, HEIGHT);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.getContentPane().add(new GamePanel(gameInstance, player));

        HomePage.this.setVisible(false);
        gameFrame.setVisible(true);
    }


    public ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
}
