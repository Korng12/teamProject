package ui;

import controllers.MenuController;
import models.User;
import models.Book;
import controllers.BookController;
import org.apache.pdfbox.pdmodel.PDDocument; // For handling PDF files
import ui.components.*;
import ui.components.Menu;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDashboard extends JPanel {
    // Instance variables
    private final User user; // Current logged-in user
    private final BookController bookController; // Controller for managing books
    private final CardLayout cardLayout; // Layout manager for switching panels
    private final JPanel cardPanel; // Main panel that holds all other panels
    private final MenuController menuController; // Controller for handling menu actions

    // Constructor
    public UserDashboard(User user, CardLayout cardLayout, JPanel cardPanel, BookController bookController) {
        this.user = user;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.bookController = bookController;
        this.menuController = new MenuController(this.user, cardLayout, cardPanel);

        // Set the layout of the dashboard to BorderLayout
        setLayout(new BorderLayout());

        // Add the header to the top of the dashboard
        add(new Header("Imagine Library", user.getName(), this::handleLogout), BorderLayout.NORTH);

        // Create the main content panel
        JPanel mainContent = new JPanel(new BorderLayout());

        // Define menu items and add the menu to the left side of the dashboard
        String[] menuItems = {"Home", "View profile", "Borrow Book", "Return Book", "Borrowed books", "Back to previous", "New Arrivals"};
        mainContent.add(new Menu(menuItems, menuController::handleMenuButtonClick), BorderLayout.WEST);

        // Add the main content (search bar and book list) to the center of the dashboard
        mainContent.add(createMainContent(), BorderLayout.CENTER);

        // Add the main content panel to the dashboard
        add(mainContent, BorderLayout.CENTER);
    }

<<<<<<< HEAD
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
=======
    // Method to create the main content panel (search bar and book list)
    // Method to create the main content panel (search bar and book list)
>>>>>>> 37b772dc60ef6abbc6ecbf4a20d37f3f0daf083b
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel to hold the search bar and book list
        JPanel bookContentPanel = new JPanel();
        bookContentPanel.setLayout(new BoxLayout(bookContentPanel, BoxLayout.Y_AXIS));
        bookContentPanel.setBackground(Color.WHITE);

        // Fetch all books and group them by genre
        Map<String, List<Book>> booksByGenre = groupBooksByGenre(bookController.getAllBooks());

        // Add genre sections to the book content panel
        for (Map.Entry<String, List<Book>> entry : booksByGenre.entrySet()) {
            JPanel genrePanel = createGenreSection(entry.getKey(), entry.getValue());
            bookContentPanel.add(genrePanel);
        }

        // Wrap the book content in a scroll pane for vertical scrolling
        JScrollPane scrollPane = new JScrollPane(bookContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Create and add the search bar at the top of the main content
        SearchBar searchBar = new SearchBar(bookController, results -> {
            // Clear and refresh the book content panel with search results
            bookContentPanel.removeAll();
            if (results.isEmpty()) {
                JLabel noResultsLabel = new JLabel("No books found for the given search criteria.");
                noResultsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                bookContentPanel.add(noResultsLabel);
            } else {
                Map<String, List<Book>> searchResultsByGenre = groupBooksByGenre(results);
                for (Map.Entry<String, List<Book>> entry : searchResultsByGenre.entrySet()) {
                    JPanel genrePanel = createGenreSection(entry.getKey(), entry.getValue());
                    bookContentPanel.add(genrePanel);
                }
            }
            bookContentPanel.revalidate();
            bookContentPanel.repaint();
        });

        mainContent.add(searchBar, BorderLayout.NORTH);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        return mainContent;
    }

    // Helper method to group books by genre
    private Map<String, List<Book>> groupBooksByGenre(List<Book> books) {
        Map<String, List<Book>> booksByGenre = new HashMap<>();
        for (Book book : books) {
            booksByGenre.computeIfAbsent(book.getGenre(), genre -> new ArrayList<>()).add(book);
        }
        return booksByGenre;
    }


    // Method to create a genre section with books and pagination
    private JPanel createGenreSection(String genre, List<Book> books) {
        JPanel genrePanel = new JPanel(new BorderLayout());
        genrePanel.setBackground(Color.WHITE);

        // Add the genre title to the top of the genre panel
        JLabel genreLabel = new JLabel(genre);
        genreLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font and size
        genreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding
        genrePanel.add(genreLabel, BorderLayout.NORTH);

        // Create a panel to hold the list of books
        JPanel bookListPanel = new JPanel();
        bookListPanel.setLayout(new BoxLayout(bookListPanel, BoxLayout.X_AXIS)); // Horizontal layout
        bookListPanel.setBackground(Color.WHITE);

        // Pagination variables
        int booksPerPage = 6; // Number of books to display per page
        int totalPages = (int) Math.ceil((double) books.size() / booksPerPage); // Calculate total pages
        final int[] currentPage = {0}; // Track the current page

        // Function to update the displayed books based on the current page
        Runnable updateBooks = () -> {
            bookListPanel.removeAll(); // Clear the current books
            int start = currentPage[0] * booksPerPage; // Calculate the start index
            int end = Math.min(start + booksPerPage, books.size()); // Calculate the end index
            for (int i = start; i < end; i++) {
                Book book = books.get(i);
                // Create the BookCard with separate actions for Read and View Details
                BookCard bookCard = new BookCard(
                        book, // Pass the book object
                        () -> openPDF(book.getLink()), // Read action: open PDF
                        () -> showBookDetails(book)       // View Details action: navigate to book details
                );

                bookListPanel.add(bookCard); // Add the book card to the panel
            }
            bookListPanel.revalidate(); // Refresh the panel
            bookListPanel.repaint(); // Repaint the panel
        };

        // Display the initial set of books
        updateBooks.run();

        // Create pagination controls (Prev, page numbers, Next)
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(Color.white);
        JButton prevButton = new JButton("Prev");
        JButton nextButton = new JButton("Next");
        paginationPanel.add(prevButton);

        // Add page number buttons (e.g., 1, 2, 3, etc.)
        for (int i = 0; i < totalPages; i++) {
            JButton pageButton = new JButton(String.valueOf(i + 1)); // Page numbers start from 1
            int pageIndex = i; // Store the page index for the action listener
            pageButton.addActionListener(e -> {
                currentPage[0] = pageIndex; // Update the current page
                updateBooks.run(); // Refresh the book list
            });
            paginationPanel.add(pageButton);
        }

        paginationPanel.add(nextButton);

        // Add action listeners for the Prev and Next buttons
        prevButton.addActionListener(e -> {
            if (currentPage[0] > 0) {
                currentPage[0]--; // Go to the previous page
                updateBooks.run(); // Refresh the book list
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage[0] < totalPages - 1) {
                currentPage[0]++; // Go to the next page
                updateBooks.run(); // Refresh the book list
            }
        });

        // Add the book list panel and pagination controls to the genre panel
        genrePanel.add(bookListPanel, BorderLayout.CENTER);
        genrePanel.add(paginationPanel, BorderLayout.SOUTH);

        return genrePanel;
    }

    // Method to show book details
    private void showBookDetails(Book book) {
        // Create the book details screen
        BookDetails detailsScreen = new BookDetails(book, cardLayout, cardPanel,user,menuController);
        cardPanel.add(detailsScreen, "BookDetails"); // Add the details screen to the card panel
        cardLayout.show(cardPanel, "BookDetails"); // Switch to the book details screen
    }

    // Method to handle logout
    private void handleLogout() {
        cardLayout.show(cardPanel, "Login"); // Switch to the login panel
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE); // Show logout message
    }

    // Method to open a PDF file
    private void openPDF(String pdfPath) {
        // Check if the PDF path is valid
        if (pdfPath == null || pdfPath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "The PDF file path is empty or invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the PDF file exists
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            JOptionPane.showMessageDialog(this, "The PDF file does not exist or is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Load the PDF document to check if it's valid
            PDDocument document = PDDocument.load(pdfFile);
            document.close();

            // Open the PDF viewer
            PDFViewer pdfViewer = new PDFViewer(pdfPath, cardLayout, cardPanel);
            cardPanel.add(pdfViewer, "PDFViewer");
            cardLayout.show(cardPanel, "PDFViewer"); // Switch to the PDF viewer panel

        } catch (IOException e) {
            // Handle errors when opening the PDF
            JOptionPane.showMessageDialog(this, "Error opening the PDF file: The file may be corrupted or inaccessible.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}