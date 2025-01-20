package ui;

import controllers.AdminController;
import controllers.BookController;
import controllers.UserController;
import models.User;
import utils.FileUtils;
import utils.ValidationUtils;
import utils.createStyledButton;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static utils.PlaceHolder.addPasswordPlaceholder;
import static utils.PlaceHolder.addPlaceholder;

public class LoginUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String uniqueName;
    private File selectedFile;
    private UserController userController = new UserController();

    public LoginUI() {
        this.setSize(1000, 700);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Login Page");
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLoginForm(), "Login");
        cardPanel.add(createRegistrationForm(), "Registration");
        this.add(cardPanel);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setVisible(true);
        this.setResizable(false);
    }

    private JPanel createLoginForm() {
        JPanel loginContainer = new JPanel(new BorderLayout());
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/thumbnails/library.jpg"));
        Image scaledImage = imageIcon.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon);

        JPanel imgPanel = new JPanel();
        imgPanel.add(imgLabel);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Login");
        JLabel welcome = new JLabel("Welcome to Imagine Library");
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        addPlaceholder(emailField, "Enter your email");
        addPasswordPlaceholder(passwordField, "Enter your password");

        emailField.setMaximumSize(new Dimension(300, 30));
        passwordField.setMaximumSize(new Dimension(300, 30));

        JButton loginButton = createStyledButton.create("Login", new Color(153, 153, 255));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel createAccPanel = createLinkPanel("Don't have an account?", "Create one", () -> cardLayout.show(cardPanel, "Registration"));

        loginButton.addActionListener(e -> handleLogin(emailField, passwordField));

        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(welcome);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(emailField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(createAccPanel);

        loginContainer.add(imgPanel, BorderLayout.WEST);
        loginContainer.add(loginPanel, BorderLayout.CENTER);

        return loginContainer;
    }

    private JPanel createRegistrationForm() {
        ImageIcon imageIcon = new ImageIcon("src\\images\\thumbnails\\library.jpg");
        Image scaledImage = imageIcon.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon);

        JPanel imgPanel = new JPanel();
        imgPanel.add(imgLabel);

        JPanel registrationContainer = new JPanel(new BorderLayout());
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel welcome = new JLabel("Welcome to Imagine Library");
        welcome.setFont(new Font("Arial", Font.PLAIN, 16));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        addPlaceholder(nameField, "Enter your name");
        addPlaceholder(emailField, "Enter your email");
        addPasswordPlaceholder(passwordField, "Enter your password");

        nameField.setMaximumSize(new Dimension(300, 30));
        emailField.setMaximumSize(new Dimension(300, 30));
        passwordField.setMaximumSize(new Dimension(300, 30));

        JButton uploadButton = createStyledButton.create("Choose profile", new Color(153, 153, 255));
        JLabel filePathLabel = new JLabel();
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        filePathLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        uploadButton.addActionListener(e -> handleFileUpload(filePathLabel));

        JPanel backToLoginPanel = createLinkPanel("Back to login", "", () -> cardLayout.show(cardPanel, "Login"));

        JButton registerBtn = createStyledButton.create("Register", new Color(153, 153, 255));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegistration(nameField, emailField, passwordField, filePathLabel));

        registerPanel.add(titleLabel);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(welcome);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(nameField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(emailField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(passwordField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(uploadButton);
        registerPanel.add(filePathLabel);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(registerBtn);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(backToLoginPanel);

        registrationContainer.add(imgPanel, BorderLayout.WEST);
        registrationContainer.add(registerPanel, BorderLayout.CENTER);

        return registrationContainer;
    }

    private JPanel createLinkPanel(String text, String linkText, Runnable action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel textLabel = new JLabel(text);
        JLabel linkLabel = new JLabel(linkText);
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        panel.add(textLabel);
        panel.add(linkLabel);
        return panel;
    }

    private void showPanel(JPanel panel, String panelName) {
        if (cardPanel.getComponentCount() > 0 && cardPanel.getComponent(0).getClass().equals(panel.getClass())) {
            // Panel already exists, just show it
            cardLayout.show(cardPanel, panelName);
        } else {
            // Add the panel to cardPanel and show it
            cardPanel.add(panel, panelName);
            cardLayout.show(cardPanel, panelName);
        }
    }

    private void handleLogin(JTextField emailField, JPasswordField passwordField) {
        String inputEmail = emailField.getText().trim();
        char[] inputPassword = passwordField.getPassword();

        if (inputEmail.isEmpty() || inputEmail.equals("Enter your email")) {
            showError("Please enter an email!");
        } else if (!ValidationUtils.isValidEmail(inputEmail)) {
            showError("Please enter a valid email address!");
        } else if (inputPassword.length == 0 || new String(inputPassword).equals("Enter your password")) {
            showError("Please enter your password!");
        } else {
            if (userController.login(inputEmail, new String(inputPassword))) {
                User loggedInUser = userController.getUserByEmail(inputEmail);
                if (loggedInUser.getRole().equals("admin")) {
                    // Debug: Print cardPanel components
                    System.out.println("CardPanel Components: " + cardPanel.getComponentCount());
                    for (Component comp : cardPanel.getComponents()) {
                        System.out.println("Component: " + comp.getClass().getSimpleName());
                    }

                    // Check if AdminDashboard already exists in cardPanel
                    boolean adminDashboardExists = false;
                    for (Component comp : cardPanel.getComponents()) {
                        if (comp instanceof AdminDashboard) {
                            adminDashboardExists = true;
                            break;
                        }
                    }

                    if (adminDashboardExists) {
                        // AdminDashboard already exists, just show it
                        cardLayout.show(cardPanel, "AdminDashboard");
                    } else {
                        // Initialize AdminDashboard and add it to cardPanel
                        AdminController adminController = new AdminController();
                        AdminDashboard adminDashboard = new AdminDashboard(loggedInUser, adminController, cardLayout, cardPanel);
                        cardPanel.add(adminDashboard, "AdminDashboard"); // Add AdminDashboard to cardPanel
                        cardLayout.show(cardPanel, "AdminDashboard"); // Show AdminDashboard
                    }
                } else {
                    // Debug: Print cardPanel components
                    System.out.println("CardPanel Components: " + cardPanel.getComponentCount());
                    for (Component comp : cardPanel.getComponents()) {
                        System.out.println("Component: " + comp.getClass().getSimpleName());
                    }

                    // Check if UserDashboard already exists in cardPanel
                    boolean userDashboardExists = false;
                    for (Component comp : cardPanel.getComponents()) {
                        if (comp instanceof UserDashboard) {
                            userDashboardExists = true;
                            break;
                        }
                    }

                    if (userDashboardExists) {
                        // UserDashboard already exists, just show it
                        cardLayout.show(cardPanel, "Dashboard");
                    } else {
                        // Initialize UserDashboard and add it to cardPanel
                        UserDashboard userDashboard = new UserDashboard(loggedInUser, cardLayout, cardPanel, new BookController());
                        cardPanel.add(userDashboard, "Dashboard"); // Add UserDashboard to cardPanel
                        cardLayout.show(cardPanel, "Dashboard"); // Show UserDashboard
                    }
                    setResizable(true);
                    showSuccess("Hello, welcome!");
                }
            } else {
                showError("Invalid credentials. Please try again.");
            }

        }
        addPlaceholder(emailField,"Enter your email");
        addPasswordPlaceholder(passwordField,"Enter your password");
    }
    private void handleFileUpload(JLabel filePathLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose your profile picture");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathLabel.setText("Selected: " + selectedFile.getName());
        } else {
            filePathLabel.setText("No file selected");
        }
    }

    private void handleRegistration(JTextField nameField, JTextField emailField, JPasswordField passwordField, JLabel filePathLabel) {
        String inputName = nameField.getText().trim();
        String inputEmail = emailField.getText().trim();
        char[] inputPassword = passwordField.getPassword();

        if (inputName.isEmpty() || inputName.equals("Enter your name")) {
            showError("Please enter your name!");
        } else if (inputEmail.isEmpty() || inputEmail.equals("Enter your email")) {
            showError("Please enter an email!");
        } else if (!ValidationUtils.isValidEmail(inputEmail)) {
            showError("Please enter a valid email address!");
        } else if (inputPassword.length == 0 || new String(inputPassword).equals("Enter your password")) {
            showError("Please enter a password!");
        } else if (selectedFile == null) {
            showError("Please select your profile picture!");
        } else {
            uniqueName = FileUtils.saveImage(selectedFile);
            if (uniqueName == null) {
                showError("Error saving profile picture!");
                return;
            }

            boolean registrationSuccess = userController.register(inputName, inputEmail, new String(inputPassword), uniqueName);
            if (registrationSuccess) {
                showSuccess("Registration successful! Please login.");
                cardLayout.show(cardPanel, "Login");
                clearFields(nameField, emailField, passwordField, filePathLabel);
            } else {
                showError("Registration failed. Please try again.");
            }
        }
    }

    private void clearFields(JTextField nameField, JTextField emailField, JPasswordField passwordField, JLabel filePathLabel) {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        filePathLabel.setText("");
        addPlaceholder(nameField, "Enter your name");
        addPlaceholder(emailField, "Enter your email");
        addPasswordPlaceholder(passwordField, "Enter your password");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}