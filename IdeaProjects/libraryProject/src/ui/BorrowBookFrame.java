package ui;

import utils.createStyledButton;
import controllers.BookController;
import controllers.MenuController;
import controllers.TransactionController;
import Listener.TransactionListener;
import models.Book;
import models.Transaction;
import models.User;
import ui.components.BookCard;
import ui.components.Header;
import ui.components.Menu;
import ui.components.SearchBar;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BorrowBookFrame extends JPanel implements TransactionListener {
    private final User user;
    private final BookController bookController;
    private final TransactionController transactionController;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final Map<Book, BookCard> bookCardMap = new HashMap<>(); // Track BookCard instances

    public BorrowBookFrame(User user, CardLayout cardLayout, JPanel cardPanel, BookController bookController, TransactionController transactionController) {
        this.user = user;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.bookController = bookController;
        this.transactionController = transactionController;

        setLayout(new BorderLayout());
        add(new Header("Imagine Library", user.getName(), this::handleLogout), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        String[] menuItems = {"Home", "View profile", "Borrow Book", "Return Book", "Borrowed books", "Back to previous", "New Arrivals"};
        mainContent.add(new Menu(menuItems, new MenuController(user, cardLayout, cardPanel)::handleMenuButtonClick), BorderLayout.WEST);
        mainContent.add(createMainContent(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

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
        Map<String, List<Book>> booksByGenre = groupAvailableBooksByGenre(bookController.getAllBooks());

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
                Map<String, List<Book>> searchResultsByGenre = groupAvailableBooksByGenre(results);
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

    private Map<String, List<Book>> groupAvailableBooksByGenre(List<Book> books) {
        Map<String, List<Book>> booksByGenre = new HashMap<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                booksByGenre.computeIfAbsent(book.getGenre(), genre -> new ArrayList<>()).add(book);
            }
        }
        return booksByGenre;
    }

    private JPanel createGenreSection(String genre, List<Book> books) {
        JPanel genrePanel = new JPanel(new BorderLayout());
        genrePanel.setBackground(Color.WHITE);

        JLabel genreLabel = new JLabel(genre);
        genreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        genreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        genrePanel.add(genreLabel, BorderLayout.NORTH);

        JPanel bookListPanel = new JPanel();
        bookListPanel.setLayout(new BoxLayout(bookListPanel, BoxLayout.X_AXIS));
        bookListPanel.setBackground(Color.WHITE);

        int booksPerPage = 6;
        int totalPages = (int) Math.ceil((double) books.size() / booksPerPage);
        final int[] currentPage = {0};

        Runnable updateBooks = () -> {
            bookListPanel.removeAll();
            int start = currentPage[0] * booksPerPage;
            int end = Math.min(start + booksPerPage, books.size());
            for (int i = start; i < end; i++) {
                Book book = books.get(i);
                BookCard bookCard = new BookCard(book, () -> borrowBook(book), () -> showBookDetails(book));
                bookListPanel.add(bookCard);
                bookCardMap.put(book, bookCard); // Track the BookCard
            }
            bookListPanel.revalidate();
            bookListPanel.repaint();
        };

        updateBooks.run();

        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(Color.WHITE);
        JButton prevButton = createStyledButton.create("Prev", new Color(90, 160, 255));
        JButton nextButton = createStyledButton.create("Next", new Color(90, 160, 255));
        paginationPanel.add(prevButton);

        for (int i = 0; i < totalPages; i++) {
            JButton pageButton = createStyledButton.create(String.valueOf(i + 1), new Color(90, 160, 255));
            int pageIndex = i;
            pageButton.addActionListener(_ -> {
                currentPage[0] = pageIndex;
                updateBooks.run();
            });
            paginationPanel.add(pageButton);
        }

        paginationPanel.add(nextButton);

        prevButton.addActionListener(_ -> {
            if (currentPage[0] > 0) {
                currentPage[0]--;
                updateBooks.run();
            }
        });

        nextButton.addActionListener(_ -> {
            if (currentPage[0] < totalPages - 1) {
                currentPage[0]++;
                updateBooks.run();
            }
        });

        genrePanel.add(bookListPanel, BorderLayout.CENTER);
        genrePanel.add(paginationPanel, BorderLayout.SOUTH);

        return genrePanel;
    }

    private void borrowBook(Book book) {
        // Create a custom dialog panel
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        // Main panel with custom styling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 160, 255), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Confirm Borrowing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message
        JLabel messageLabel = new JLabel("<html><center>Are you sure you want to borrow<br>'" + book.getTitle() + "'?</center></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        // Confirm button
        JButton confirmButton = createStyledButton.create("Confirm", new Color(90, 160, 255));
        confirmButton.addActionListener(_ -> {
            dialog.dispose();
            boolean success = transactionController.borrowBook(book, user, this);
            if (!success) {
                showErrorDialog("Failed to borrow the book. Please try again.");
            }
        });

        // Cancel button
        JButton cancelButton = createStyledButton.create("Cancel", Color.GRAY);
        cancelButton.addActionListener(_ -> dialog.dispose());

        // Add components to the panel
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        // Add panel to dialog
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JDialog errorDialog = new JDialog();
        errorDialog.setUndecorated(true);
        errorDialog.setSize(350, 150);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setModal(true);

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
        errorPanel.setBackground(Color.WHITE);
        errorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel errorTitle = new JLabel("Error");
        errorTitle.setFont(new Font("Arial", Font.BOLD, 18));
        errorTitle.setForeground(Color.RED);
        errorTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errorMessage = new JLabel("<html><center>" + message + "</center></html>");
        errorMessage.setFont(new Font("Arial", Font.PLAIN, 14));
        errorMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton okButton = createStyledButton.create("OK", Color.RED);
        okButton.addActionListener(_ -> errorDialog.dispose());
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorPanel.add(errorTitle);
        errorPanel.add(Box.createVerticalStrut(15));
        errorPanel.add(errorMessage);
        errorPanel.add(Box.createVerticalStrut(15));
        errorPanel.add(okButton);

        errorDialog.add(errorPanel);
        errorDialog.setVisible(true);
    }

    private void showBookDetails(Book book) {
        BookDetails detailsScreen = new BookDetails(book, cardLayout, cardPanel, user, new MenuController(user, cardLayout, cardPanel));
        cardPanel.add(detailsScreen, "BookDetails");
        cardLayout.show(cardPanel, "BookDetails");
    }

    private void handleLogout() {
        cardLayout.show(cardPanel, "Login");
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onBorrowSuccess(Transaction transaction) {
        // Show success message
        JOptionPane.showMessageDialog(this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Update the specific BookCard
        Book borrowedBook = bookController.getBooksByID(transaction.getBookId());
        BookCard bookCard = bookCardMap.get(borrowedBook);
        if (bookCard != null) {
            bookCard.updateAvailability(); // Update the UI to reflect the book's unavailability
        }
    }

    @Override
    public void onBorrowFailure(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onReturnSuccess(Transaction transaction) {
        // Not used in BorrowBookFrame
    }

    @Override
    public void onReturnFailure(String errorMessage) {
        // Not used in BorrowBookFrame
    }
}