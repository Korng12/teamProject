package ui;

import controllers.MenuController;
import models.User;
import models.Book;
import controllers.BookController;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import utils.createStyledButton;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class UserDashboard extends JPanel {
    private User user; // Current logged-in user
    private BookController bookController; // Handles book-related operations
    private CardLayout cardLayout; // For switching between panels
    private JPanel cardPanel; // Main container for panels
    private MenuController menuController;
    private static final int BOOK_CARD_WIDTH = 180; // Width of each book card
    private static final int BOOK_CARD_HEIGHT = 260; // Height of each book card
    private Point dragStart;
    private final Map<String, JScrollPane> genreScrollPanes = new HashMap<>();
    private static final int DRAG_SPEED = 20;

    // Constructor
    public UserDashboard(User user, CardLayout cardLayout, JPanel cardPanel, BookController bookController) {
        this.user = user;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.bookController = bookController;
        this.menuController = new MenuController(this.user, cardLayout, cardPanel);

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
        header.setPreferredSize(new Dimension(getWidth(), 80)); // Set height of the header

        // Logo and library name section
        JPanel logoSection = new JPanel();
        logoSection.setOpaque(false); // Transparent background
        logoSection.setLayout(new BoxLayout(logoSection, BoxLayout.X_AXIS)); // Horizontal layout
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
        userSection.setLayout(new BoxLayout(userSection, BoxLayout.X_AXIS)); // Horizontal layout
        userSection.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Logout button
        JButton logoutButton = createStyledButton.create("Log out", new Color(136, 30, 30)); // Red button
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
        header.add(logoSection, BorderLayout.WEST); // Logo and library name on the left
        header.add(welcomePanel, BorderLayout.CENTER); // Welcome message in the center
        header.add(userSection, BorderLayout.EAST); // Logout button on the right

        return header;
    }

    // Method to create the menu panel
    private JPanel createMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS)); // Vertical layout
        menuPanel.setBackground(new Color(204, 204, 255)); // Light purple background
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Padding

        // Menu items
        String[] menuItems = {"Home", "View profile", "Borrow Book", "Return Book", "Borrowed books", "Back to previous", "New Arrivals"};
        for (String item : menuItems) {
            JButton button = createStyledButton.create(item, new Color(153, 153, 255)); // Purple button
            button.setAlignmentX(Component.LEFT_ALIGNMENT); // Align buttons to the left
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Fill width

            // Add action listener to the button
            button.addActionListener(e -> menuController.handleMenuButtonClick(item));

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
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS)); // Vertical layout for genre sections
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Add search panel to the main content
        mainContent.add(createSearchPanel());

        // Fetch all books from the controller
        List<Book> books = bookController.getAllBooks();

        // Group books by genre
        Map<String, List<Book>> booksByGenre = new HashMap<>();
        for (Book book : books) {
            String genre = book.getGenre(); // Assuming Book has a getGenre() method
            booksByGenre.computeIfAbsent(genre, k -> new ArrayList<>()).add(book);
        }

        // Create a section for each genre
        for (Map.Entry<String, List<Book>> entry : booksByGenre.entrySet()) {
            String genre = entry.getKey();
            List<Book> genreBooks = entry.getValue();

            // Create a panel for the genre
            JPanel genrePanel = new JPanel();
            genrePanel.setLayout(new BoxLayout(genrePanel, BoxLayout.Y_AXIS)); // Vertical layout
            genrePanel.setBackground(Color.WHITE);

            // Add genre title
            JLabel genreLabel = new JLabel(genre);
            genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align the label to the left
            genreLabel.setFont(new Font("Arial", Font.BOLD, 18));
            genreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding

// Ensure the genrePanel uses a layout that allows left-alignment
            genrePanel.setLayout(new BoxLayout(genrePanel, BoxLayout.Y_AXIS)); // Keep vertical layout

// Add the genre label to the genre panel
            genrePanel.add(genreLabel);


            // Create a horizontal panel for book cards
            JPanel bookListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Horizontal layout
            bookListPanel.setBackground(Color.WHITE);

            // Add book cards to the genre
            for (Book book : genreBooks) {
                JPanel bookPanel = createBookPanel(book); // Create a panel for each book
                bookListPanel.add(bookPanel);
            }

            // Add the book list to the genre panel
            genrePanel.add(bookListPanel);

            // Add the genre panel to the main content
            mainContent.add(genrePanel);
        }

        // Wrap the main content in a scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    private JPanel createSearchPanel() {
        // Search bar and filter panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow components to expand horizontally

        // Search icon
        ImageIcon searchIcon = loadImageIcon("/images/icons/search_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png", 20, 20);
        JLabel searchIconLabel = new JLabel(searchIcon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // Do not expand the icon
        searchPanel.add(searchIconLabel, gbc);

        // Search text field
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
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
        gbc.weightx = 0.0; // Do not expand the dropdown
        searchPanel.add(filterDropdown1, gbc);

        String[] filters2 = {"Author", "Language"};
        JComboBox<String> filterDropdown2 = new JComboBox<>(filters2);
        filterDropdown2.setFont(new Font("Arial", Font.PLAIN, 14));
        filterDropdown2.setBackground(Color.WHITE);
        filterDropdown2.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // Do not expand the dropdown
        searchPanel.add(filterDropdown2, gbc);

        return searchPanel;
    }
    // Method to create a book panel
    private JPanel createBookPanel(Book book) {
        // Create the main book card panel
        JPanel bookCard = new JPanel();
        bookCard.setLayout(new BoxLayout(bookCard, BoxLayout.Y_AXIS)); // Vertical layout
        bookCard.setPreferredSize(new Dimension(BOOK_CARD_WIDTH, BOOK_CARD_HEIGHT));
        bookCard.setBackground(Color.WHITE);
        bookCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1), // Light gray border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the card
        ));

        // Load the book image
        ImageIcon originalIcon = loadImageIcon(book.getImgPath(), 120, 160);
        if (originalIcon.getImage() == null) {
            System.err.println("Image not found: " + book.getImgPath()); // Debug statement
        }
        JLabel bookImage = new JLabel(originalIcon);
        bookImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create and style the title label
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50)); // Dark gray text
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create and style the author label
        JLabel authorLabel = new JLabel("By " + book.getAuthor());
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        authorLabel.setForeground(new Color(100, 100, 100)); // Medium gray text
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create and style the Read button
        JButton readButton = new JButton("Read");
        readButton.setFont(new Font("Arial", Font.PLAIN, 12));
        readButton.setBackground(new Color(90, 160, 255)); // Light blue background
        readButton.setForeground(Color.WHITE);
        readButton.setFocusPainted(false);
        readButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add action listener to handle the button click (for example, open the book's PDF or navigate to a new view)
        readButton.addActionListener(e -> {
            openPDF(book.getLink()); // Open the PDF file associated with the book
        });

        // Add components to the book card
        bookCard.add(bookImage);
        bookCard.add(Box.createVerticalStrut(10)); // Add some vertical spacing
        bookCard.add(titleLabel);
        bookCard.add(Box.createVerticalStrut(5));
        bookCard.add(authorLabel);
        bookCard.add(Box.createVerticalStrut(10)); // Add some vertical spacing before the button
        bookCard.add(readButton); // Add the Read button

        // Add hover effect
        bookCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bookCard.setBackground(new Color(245, 245, 245)); // Light gray background on hover
                bookCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Darker border on hover
                        BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding for shadow effect
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                bookCard.setBackground(Color.WHITE); // Reset background color
                bookCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Reset border
                        BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding for shadow effect
                ));
            }
        });
        return bookCard;
    }

    private void openPDF(String pdfPath) {
        try {
            // Load the PDF document
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDFRenderer renderer = new PDFRenderer(document);

            // Create a panel to hold the PDF viewer and controls
            JPanel pdfViewerPanel = new JPanel(new BorderLayout());
            pdfViewerPanel.setBackground(new Color(245, 245, 245)); // Light gray background
            pdfViewerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

            // Create a title bar for the PDF viewer
            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(new Color(60, 63, 65)); // Dark background
            titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Add padding

            // Add a title label
            JLabel titleLabel = new JLabel("PDF Viewer: " + new File(pdfPath).getName());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE); // White text
            titleBar.add(titleLabel, BorderLayout.WEST);

            // Add the title bar to the viewer panel
            pdfViewerPanel.add(titleBar, BorderLayout.NORTH);

            // Create a panel to hold the PDF pages
            JPanel pdfPagesPanel = new JPanel();
            pdfPagesPanel.setLayout(new BoxLayout(pdfPagesPanel, BoxLayout.Y_AXIS)); // Default to vertical mode
            pdfPagesPanel.setBackground(Color.WHITE);

            // Render each page of the PDF and add it to the panel
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                int pageIndex = i;
                JPanel pagePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        try {
                            // Render the page to the panel
                            renderer.renderPageToGraphics(pageIndex, (Graphics2D) g);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                pagePanel.setPreferredSize(new Dimension(600, 800)); // Adjust size for each page
                pagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Add a border
                pdfPagesPanel.add(pagePanel);
                pdfPagesPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between pages
            }

            // Create a scroll pane for the PDF pages
            JScrollPane pdfScrollPane = new JScrollPane(pdfPagesPanel);
            pdfScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            pdfScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            pdfScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border

            // Customize the scroll bars
            JScrollBar verticalScrollBar = pdfScrollPane.getVerticalScrollBar();
            verticalScrollBar.setUnitIncrement(16); // Smoother scrolling
            verticalScrollBar.setBackground(new Color(245, 245, 245)); // Match background

            // Add the PDF scroll pane to the viewer panel
            pdfViewerPanel.add(pdfScrollPane, BorderLayout.CENTER);

            // Create a control panel for buttons
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            controlPanel.setBackground(new Color(245, 245, 245)); // Light gray background
            controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

            // Button to toggle between vertical and horizontal modes
            JButton toggleModeButton = createStyledButton.create("Switch to Horizontal Mode", new Color(90, 160, 255));
            toggleModeButton.setFont(new Font("Arial", Font.BOLD, 14));
            toggleModeButton.setFocusPainted(false); // Remove focus border
            toggleModeButton.addActionListener(e -> {
                if (pdfPagesPanel.getLayout() instanceof BoxLayout) {
                    // Switch to horizontal mode
                    pdfPagesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
                    toggleModeButton.setText("Switch to Vertical Mode");
                } else {
                    // Switch to vertical mode
                    pdfPagesPanel.setLayout(new BoxLayout(pdfPagesPanel, BoxLayout.Y_AXIS));
                    toggleModeButton.setText("Switch to Horizontal Mode");
                }
                pdfPagesPanel.revalidate();
                pdfPagesPanel.repaint();
            });
            controlPanel.add(toggleModeButton);

            // Button to return to the library view
            JButton backButton = createStyledButton.create("Back to Library", new Color(180, 34, 34));
            backButton.setFont(new Font("Arial", Font.BOLD, 14));
            backButton.setFocusPainted(false); // Remove focus border
            backButton.addActionListener(e -> {
                cardLayout.show(cardPanel, "Dashboard"); // Switch back to the library view
                try {
                    document.close(); // Close the PDF document
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            controlPanel.add(backButton);

            // Add the control panel to the viewer panel
            pdfViewerPanel.add(controlPanel, BorderLayout.SOUTH);

            // Add the PDF viewer panel to the card panel
            cardPanel.add(pdfViewerPanel, "PDFViewer");
            cardLayout.show(cardPanel, "PDFViewer"); // Switch to the PDF viewer

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening PDF file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to load and resize images
    private ImageIcon loadImageIcon(String path, int width, int height) {
        ImageIcon icon = null;
        try {
            // Ensure the path starts with a '/' if it's an absolute path
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            icon = new ImageIcon(getClass().getResource(path));
            if (icon.getImage() == null) {
                throw new NullPointerException("Image not found at the specified path: " + path);
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