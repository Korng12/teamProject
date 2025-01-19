package ui;

import controllers.BookController;
import controllers.MenuController;
import controllers.TransactionController;
import models.Book;
import models.User;
import ui.components.*;
import ui.components.Menu;
import utils.BorrowBookUtil;
import utils.ImageLoader;
import utils.createStyledButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookDetails extends JPanel {
    private final Book book;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final User user;
    private final MenuController menuController;
    private final BookController bookController;
    private final String buttonText;
    private final TransactionController transactionController;
    private JPanel mainContent;
    private JPanel recommendationsPanel;

    public BookDetails(Book book, String buttonText, CardLayout cardLayout, JPanel cardPanel, User user, MenuController menuController, BookController bookController) {
        this.book = book;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.user = user;
        this.menuController = menuController;
        this.bookController = bookController;
        this.buttonText = buttonText;
        this.transactionController = new TransactionController();
        this.mainContent = new JPanel(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        add(new Header("Imagine Library", user.getName(), this::handleLogout), BorderLayout.NORTH);

        String[] menuItems = {"Home", "View profile", "Borrow Book", "Return Book", "Borrowed books", "Back to previous", "New Arrivals"};
        mainContent.add(new Menu(menuItems, menuController::handleMenuButtonClick), BorderLayout.WEST);

        mainContent.add(createMainContent(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add the book details panel
        mainPanel.add(createBookDetailsPanel());
        mainPanel.add(Box.createVerticalStrut(20));

        // Add the action button (e.g., Borrow or Read)
        mainPanel.add(createActionButton());
        mainPanel.add(Box.createVerticalStrut(20));

        // Add the recommendations panel (initially populated with books of the same genre)
        recommendationsPanel = createRecommendationsPanel(bookController.getBooksByGenre(book.getGenre()));
        mainPanel.add(recommendationsPanel);

        return mainPanel;
    }

    private JPanel createBookDetailsPanel() {
        JPanel bookDetailsPanel = new JPanel(new BorderLayout(20, 20)); // Add horizontal and vertical gaps
        bookDetailsPanel.add(createBookCover(), BorderLayout.WEST);
        bookDetailsPanel.add(createBookInfoPanel(), BorderLayout.CENTER);
        return bookDetailsPanel;
    }

    private JLabel createBookCover() {
        JLabel bookCover = new JLabel();
        try {
            ImageIcon icon = ImageLoader.loadImageIcon(book.getImgPath(), 200, 300);
            bookCover.setIcon(icon);
        } catch (Exception e) {
            bookCover.setText("Error loading image");
            bookCover.setFont(new Font("Arial", Font.ITALIC, 12));
            bookCover.setForeground(Color.RED);
        }
        bookCover.setPreferredSize(new Dimension(200, 300));
        bookCover.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return bookCover;
    }

    private JPanel createBookInfoPanel() {
        JPanel bookInfoPanel = new JPanel();
        bookInfoPanel.setLayout(new BoxLayout(bookInfoPanel, BoxLayout.Y_AXIS));
        bookInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Add title, author, and ISBN
        addLabel(bookInfoPanel, "Title: " + book.getTitle(), Font.BOLD, 18);
        addLabel(bookInfoPanel, "Author: " + book.getAuthor(), Font.PLAIN, 16);
        addLabel(bookInfoPanel, "ISBN: " + book.getIsbn(), Font.PLAIN, 14);

        // Add description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookInfoPanel.add(descriptionLabel);

        JTextArea descriptionArea = new JTextArea(book.getDescription());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(this.getBackground());
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookInfoPanel.add(descriptionArea);

        return bookInfoPanel;
    }

    private void addLabel(JPanel panel, String text, int fontStyle, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
    }

    private JButton createActionButton() {
        String buttonLabel = book.isAvailable() ? buttonText : "Unavailable";
        JButton button = createStyledButton.create(buttonLabel, book.isAvailable() ? new Color(0, 100, 225) : Color.GRAY);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setEnabled(book.isAvailable());
        button.addActionListener(e -> handleButtonAction());

        // Add padding and margin to the button
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        return button;
    }

    private void handleButtonAction() {
        if (!book.isAvailable()) {
            JOptionPane.showMessageDialog(this, "This book is currently unavailable.", "Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (buttonText) {
            case "Borrow":
                borrowBook(book);
                break;
            case "Read":
                openPDF(book.getLink());
                break;
            default:
                JOptionPane.showMessageDialog(this, "Action not available.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private void borrowBook(Book book) {
        BorrowBookUtil.borrowBook(book, user, transactionController, this);
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

    private JPanel createRecommendationsPanel(List<Book> recommendedBooks) {
        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel recommendationsLabel = new JLabel("Recommendations");
        recommendationsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        recommendationsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        recommendationsPanel.add(recommendationsLabel);

        JPanel bookCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        for (Book recommendedBook : recommendedBooks) {
            if (!recommendedBook.getTitle().equals(book.getTitle())) {
                BookCard bookCard = new BookCard(recommendedBook, () -> handleButtonAction(), () -> showBookDetails(recommendedBook));
                bookCardsPanel.add(bookCard);
            }
        }

        recommendationsPanel.add(bookCardsPanel);
        return recommendationsPanel;
    }

    private void showBookDetails(Book book) {
        BookDetails detailsScreen = new BookDetails(book, buttonText, cardLayout, cardPanel, user, menuController, bookController);
        cardPanel.add(detailsScreen, "BookDetails");
        cardLayout.show(cardPanel, "BookDetails");
    }

    private void handleLogout() {
        cardLayout.show(cardPanel, "Login");
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }
}