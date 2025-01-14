package controllers;

import models.User;
import java.awt.CardLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class MenuController {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private User user;

    public MenuController(User user, CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.user = user;

        System.out.println("MenuController initialized with user: " + user.getName()); // Debugging statement
    }

    public void handleMenuButtonClick(String menuItem) {
        System.out.println("Button clicked: " + menuItem); // Debugging statement
        switch (menuItem) {
            case "Home":
                cardLayout.show(cardPanel, "Dashboard");
                break;
            case "View profile":
                showUserProfileDialog();
                break;
            case "Borrow Book":
                cardLayout.show(cardPanel, "BorrowBook");
                break;
            case "Return Book":
                cardLayout.show(cardPanel, "ReturnBook");
                break;
            case "Borrowed books":
                cardLayout.show(cardPanel, "BorrowedBooks");
                break;
            case "Back to previous":
                cardLayout.previous(cardPanel);
                break;
            case "New Arrivals":
                cardLayout.show(cardPanel, "NewArrivals");
                break;
            default:
                System.err.println("Unknown menu item: " + menuItem);
                break;
        }
    }
    private void showUserProfileDialog() {
        // Create a custom panel for the profile dialog
        JPanel profilePanel = new JPanel(new BorderLayout(10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Add a default photo (you can replace this with an actual image)
        ImageIcon defaultPhoto = new ImageIcon(getClass().getResource("/images/thumbnails/library.jpg")); // Path to your default photo
        if (defaultPhoto.getImage() == null) {
            System.err.println("Image not found at the specified path."); // Debugging statement
            defaultPhoto = new ImageIcon(); // Fallback if the image is not found
        }

        // Create a rounded profile picture
        JLabel photoLabel = new JLabel(new ImageIcon(getRoundedImage(defaultPhoto.getImage(), 100, 100)));
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(photoLabel, BorderLayout.NORTH);

        // Create a panel for user details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Add user information with a modern font
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        JLabel nameLabel = new JLabel("Name: " + user.getName());
        nameLabel.setFont(labelFont);
        JLabel emailLabel = new JLabel("Email: " + user.getEmail());
        emailLabel.setFont(labelFont);
        JLabel roleLabel = new JLabel("Role: " + (user.getRole() != null ? user.getRole() : "visitor"));
        roleLabel.setFont(labelFont);

        detailsPanel.add(nameLabel);
        detailsPanel.add(emailLabel);
        detailsPanel.add(roleLabel);

        profilePanel.add(detailsPanel, BorderLayout.CENTER);

        // Add a close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.setBackground(new Color(153, 153, 255)); // Purple background
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(profilePanel);
            if (window != null) {
                window.dispose(); // Close the dialog
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        buttonPanel.add(closeButton);
        profilePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create a custom JDialog
        JDialog dialog = new JDialog();
        dialog.setTitle("User Profile"); // Set the dialog title
        dialog.setModal(true); // Make the dialog modal
        dialog.setContentPane(profilePanel); // Set the custom panel as the content
        dialog.pack(); // Resize the dialog to fit the content
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close the dialog when the close button is clicked
        dialog.setVisible(true); // Show the dialog
    }
    // Helper method to create a rounded image
    private Image getRoundedImage(Image image, int width, int height) {
        BufferedImage roundedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = roundedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a rounded circle
        int diameter = Math.min(width, height);
        g2.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();

        return roundedImage;
    }
}