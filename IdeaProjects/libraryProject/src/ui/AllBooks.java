package ui;

import controllers.BookController;
import controllers.MenuController;
import models.Book;
import models.User;
import ui.components.*;
import ui.components.Menu;
import utils.createStyledButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AllBooks extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final User user;
    private final MenuController menuController;
    private final String genre;
    private final List<Book> books;
    private final BookController bookController;
    private JPanel bookListPanel;
    private int currentPage = 0;
    private final int booksPerPage = 9; // Adjust based on your layout

    public AllBooks(String genre, List<Book> books, CardLayout cardLayout, JPanel cardPanel, User user, MenuController menuController, BookController bookController) {
        this.genre = genre;
        this.books = books;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.user = user;
        this.menuController = menuController;
        this.bookController = bookController;

        setLayout(new BorderLayout());

        add(new Header("Imagine Library", user.getName(), this::handleLogout), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        String[] menuItems = {"Home", "View Profile", "Borrow Book", "Return Book", "Borrowed Books", "Back to previous", "New Arrivals"};
        mainContent.add(new Menu(menuItems, menuController::handleMenuButtonClick), BorderLayout.WEST);

        mainContent.add(createMainContent(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to Imagine Library");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        SearchBar searchBar = new SearchBar(bookController, this::updateBookList);
        mainPanel.add(searchBar, BorderLayout.NORTH);

        bookListPanel = new JPanel(new GridBagLayout());
        bookListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateBookListPanel();

        JScrollPane scrollPane = new JScrollPane(bookListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = createStyledButton.create("Prev", new Color(90, 160, 255));
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateBookListPanel();
            }
        });
        JButton nextButton = createStyledButton.create("Next", new Color(90, 160, 255));
        nextButton.addActionListener(e -> {
            if ((currentPage + 1) * booksPerPage < books.size()) {
                currentPage++;
                updateBookListPanel();
            }
        });
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        mainPanel.add(paginationPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void updateBookListPanel() {
        bookListPanel.removeAll();
        int start = currentPage * booksPerPage;
        int end = Math.min(start + booksPerPage, books.size());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int columns = calculateColumnCount();
        int row = 0, col = 0;

        for (int i = start; i < end; i++) {
            Book book = books.get(i);
            BookCard bookCard = new BookCard(
                    book,
                    () -> openPDF(book.getLink()),
                    () -> showBookDetails(book)
            );

            gbc.gridx = col;
            gbc.gridy = row;
            bookListPanel.add(bookCard, gbc);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }

        bookListPanel.revalidate();
        bookListPanel.repaint();
    }

    private int calculateColumnCount() {
        // Adjust the number of columns based on the panel width
        int panelWidth = bookListPanel.getWidth();
        int cardWidth = 200; // Approximate width of a BookCard
        return Math.max(1, panelWidth / cardWidth);
    }

    private void updateBookList(List<Book> results) {
        books.clear();
        books.addAll(results);
        currentPage = 0;
        updateBookListPanel();
    }

    private void openPDF(String pdfPath) {
        if (pdfPath == null || pdfPath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "The PDF file path is empty or invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PDFViewer pdfViewer = new PDFViewer(pdfPath, cardLayout, cardPanel);
            cardPanel.add(pdfViewer, "PDFViewer");
            cardLayout.show(cardPanel, "PDFViewer");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening the PDF file: The file may be corrupted or inaccessible.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBookDetails(Book book) {
        JOptionPane.showMessageDialog(this, "Showing details for: " + book.getTitle());
    }

    private void handleLogout() {
        cardLayout.show(cardPanel, "Login");
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }
}