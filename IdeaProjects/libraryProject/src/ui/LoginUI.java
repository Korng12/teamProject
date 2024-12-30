package ui;
import services.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LoginUI extends JFrame {

    LoginUI() {
        this.setSize(1000, 700);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Login Page");

        JPanel mainPanel = new JPanel(new BorderLayout());


        ImageIcon imageIcon = new ImageIcon("src/images/thumbnails/library.jpg");
        Image scaledImage = imageIcon.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon);

        JPanel imgPanel = new JPanel();
        imgPanel.add(imgLabel);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();


        addPlaceholder(emailField, "Enter your email");
        addPasswordPlaceholder(passwordField, "Enter your password");

        emailField.setMaximumSize(new Dimension(300, 30));
        passwordField.setMaximumSize(new Dimension(300, 30));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel result = new JLabel();
        result.setAlignmentX(Component.CENTER_ALIGNMENT);
        result.setFont(new Font("Arial", Font.PLAIN, 16));
        result.setForeground(Color.BLUE);

        loginButton.addActionListener(e -> {
            String inputEmail = emailField.getText().trim();
            char[] inputPassword = passwordField.getPassword();

            if (inputEmail.isEmpty() || inputEmail.equals("Enter your email") ) {
                JOptionPane.showMessageDialog(
                        LoginUI.this,
                        "Please enter an email!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else if (!isValidEmail(inputEmail)) {
                JOptionPane.showMessageDialog(
                        LoginUI.this,
                        "Please enter a valid email address!(email should be followed by @gmail.com)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else if (inputPassword.length == 0 || new String(inputPassword).equals("Enter your password")) {
                JOptionPane.showMessageDialog(
                        LoginUI.this,
                        "Please enter a password!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else{
                Admin admin= new Admin();
                String passwordString=new String(inputPassword);
                if(admin.verifyAdmin(inputEmail,passwordString)){
                    JOptionPane.showMessageDialog(
                            LoginUI.this,
                            "Welcome, Admin!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }else {
                    JOptionPane.showMessageDialog(
                            LoginUI.this,
                            "Invalid credentials. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });


        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(emailField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(result);

        mainPanel.add(imgPanel, BorderLayout.WEST);
        mainPanel.add(loginPanel);

        this.add(mainPanel);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setVisible(true);
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void addPasswordPlaceholder(JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0); // Makes the text visible like a placeholder

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('•'); // Hides characters when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0); // Makes the placeholder visible again
                }
            }
        });
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"; // email validation
        return email.matches(emailRegex);
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
