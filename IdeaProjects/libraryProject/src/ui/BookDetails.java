package ui;

import controllers.BookController;
import controllers.MenuController;
import models.Book;
import models.User;
import ui.components.Header;
import ui.components.Menu;
import ui.components.SearchBar;

import javax.swing.*;
import java.awt.*;

public class BookDetails extends JPanel {
    private final Book book; // The book to display details for
    private final CardLayout cardLayout; // Layout manager for switching panels
    private final JPanel cardPanel; // Main panel that holds all other panels
    private final User user; // Current logged-in user
    private final MenuController menuController;

    // Constructor
    public BookDetails(Book book, CardLayout cardLayout, JPanel cardPanel, User user, MenuController menuController) {
        this.book = book;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.user = user;
        this.menuController = menuController;

        // Set the layout of the book details panel to BorderLayout
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
    private JPanel createMainContent(){
        JPanel mainPanel = new JPanel(new BorderLayout());

        return mainPanel;
    }
    // Method to handle logout
    private void handleLogout() {
        cardLayout.show(cardPanel, "Login"); // Switch to the login panel
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE); // Show logout message
    }
}