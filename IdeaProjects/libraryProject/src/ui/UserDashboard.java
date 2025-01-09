package ui;

import models.User;
import models.Book;
import controllers.BookController;
import utils.createStyledButton;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JPanel {
    private User user; // Current logged-in user
    private BookController bookController; // Handles book-related operations
    private CardLayout cardLayout; // For switching between panels
    private JPanel cardPanel; // Main container for panels

    // Constructor
    public UserDashboard(User user, CardLayout cardLayout, JPanel cardPanel, BookController bookController) {
        this.user = user;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.bookController = bookController;

        setLayout(new BorderLayout());

        // Add the header to the top
        add(createHeader(), BorderLayout.NORTH);

        // Create the main content panel
        JPanel mainContent = new JPanel(new BorderLayout());

        // Add the menu to the left
        mainContent.add(createMenu(), BorderLayout.WEST);

        // Add the main content to the center (including search bar and book list)
        mainContent.add(createMainContent(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    // Method to create the header panel
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 153, 255)); // Purple background
        header.setPreferredSize(new Dimension(getWidth(), 80)); // Set height

        // Logo and library name section
        JPanel logoSection = new JPanel();
        logoSection.setOpaque(false); // Transparent background
        logoSection.setLayout(new BoxLayout(logoSection, BoxLayout.X_AXIS));
        logoSection.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Load and resize the logo image
        ImageIcon logoIcon = loadImageIcon("/images/thumbnails/library.jpg", 40, 40);
        JLabel logo = new JLabel(logoIcon);
        logoSection.add(logo);

        // Add spacing between logo and library name
        logoSection.add(Box.createRigidArea(new Dimension(10, 0)));

        // Add the library name
        JLabel libraryName = new JLabel("Imagine Library");
        libraryName.setFont(new Font("Arial", Font.BOLD, 20));
        libraryName.setForeground(Color.WHITE); // White text color
        logoSection.add(libraryName);

        // User section with logout button
        JPanel userSection = new JPanel();
        userSection.setOpaque(false); // Transparent background
        userSection.setLayout(new BoxLayout(userSection, BoxLayout.X_AXIS));
        userSection.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Logout button
        JButton logoutButton = createStyledButton.create("Log out", new Color(180, 34, 34)); // Red button
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);

        // Logout functionality
        logoutButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Login"); // Switch to login panel
            JOptionPane.showMessageDialog(UserDashboard.this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
        });
        userSection.add(logoutButton);

        // Welcome message in the center
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setOpaque(false); // Transparent background

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the label
        welcomePanel.add(welcomeLabel, gbc);

        // Add sections to the header
        header.add(logoSection, BorderLayout.WEST);
        header.add(welcomePanel, BorderLayout.CENTER);
        header.add(userSection, BorderLayout.EAST);

        return header;
    }

    // Method to create the menu panel
    private JPanel createMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(204, 204, 255)); // Light purple background
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Padding

        // Menu items
        String[] menuItems = {"Home", "View profile", "Borrow Book", "Return Book", "Borrowed books", "Back to previous", "New Arrivals"};
        for (String item : menuItems) {
            JButton button = createStyledButton.create(item, new Color(153, 153, 255)); // Purple button
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Fill width
            menuPanel.add(button);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between buttons
        }

        // Wrap the menu in a scroll pane
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    // Method to create the main content panel
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Search bar and filter panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components

        // Search icon
        ImageIcon searchIcon = loadImageIcon("/images/icons/search_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png", 20, 20);
        JLabel searchIconLabel = new JLabel(searchIcon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(searchIconLabel, gbc);

        // Search text field
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow the search field to expand
        searchPanel.add(searchField, gbc);

        // Filter dropdowns
        String[] filters1 = {"Category", "Year Published"};
        JComboBox<String> filterDropdown1 = new JComboBox<>(filters1);
        filterDropdown1.setFont(new Font("Arial", Font.PLAIN, 14));
        filterDropdown1.setBackground(Color.WHITE);
        filterDropdown1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0; // Prevent the dropdown from expanding
        searchPanel.add(filterDropdown1, gbc);

        String[] filters2 = {"Author", "Language"};
        JComboBox<String> filterDropdown2 = new JComboBox<>(filters2);
        filterDropdown2.setFont(new Font("Arial", Font.PLAIN, 14));
        filterDropdown2.setBackground(Color.WHITE);
        filterDropdown2.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0; // Prevent the dropdown from expanding
        searchPanel.add(filterDropdown2, gbc);

        // Add search panel to the main content
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // Display books in a scrollable panel
        JPanel bookListPanel = new JPanel();
        bookListPanel.setLayout(new BoxLayout(bookListPanel, BoxLayout.Y_AXIS));
        bookListPanel.setBackground(Color.WHITE);

        // Fetch all books from the controller
        List<Book> books = bookController.getAllBooks();
        for (Book book : books) {
            JPanel bookPanel = createBookPanel(book); // Create a panel for each book
            bookListPanel.add(bookPanel);
            bookListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between books
        }

        JScrollPane scrollPane = new JScrollPane(bookListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainContent.add(scrollPane, BorderLayout.CENTER);

        return mainContent;
    }

    // Method to create a panel for a single book
    private JPanel createBookPanel(Book book) {
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        bookPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Book title
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bookPanel.add(titleLabel, BorderLayout.NORTH);

        // Book author
        JLabel authorLabel = new JLabel("Author: " + book.getAuthor());
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bookPanel.add(authorLabel, BorderLayout.CENTER);

        // Borrow button
        JButton borrowButton = new JButton(book.isAvailable() ? "Borrow" : "Unavailable");
        borrowButton.setEnabled(book.isAvailable());
        borrowButton.addActionListener(e -> {
            boolean success = bookController.updateBookAvailability(book.getId(), false);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookList(); // Refresh the UI
            } else {
                JOptionPane.showMessageDialog(this, "Failed to borrow the book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        bookPanel.add(borrowButton, BorderLayout.SOUTH);

        return bookPanel;
    }

    // Method to refresh the book list
    private void refreshBookList() {
        removeAll(); // Clear the current content
        add(createHeader(), BorderLayout.NORTH); // Re-add the header
        add(createMainContent(), BorderLayout.CENTER); // Re-add the main content
        revalidate(); // Refresh the layout
        repaint(); // Redraw the panel
    }

    // Helper method to load and resize images
    private ImageIcon loadImageIcon(String path, int width, int height) {
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource(path));
            if (icon.getImage() == null) {
                throw new NullPointerException("Image not found at the specified path.");
            }
            Image resizedImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(resizedImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            icon = new ImageIcon(); // Fallback: Use an empty icon
        }
        return icon;
    }
}