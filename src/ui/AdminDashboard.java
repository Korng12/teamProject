
package ui;

import controllers.AdminController;
import controllers.AdminMenuController;
import models.Book;
import models.User;
import models.Visitor;
import ui.components.Header;
import ui.components.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
        import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminDashboard extends JPanel {
    private final User user;
    private final AdminController adminController;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final CardLayout innerCardLayout;
    private final JPanel innerCardPanel;



    // Constants for colors, fonts, and padding
    private static final Color CARD_BACKGROUND = new Color(230, 230, 250);
    private static final Color CARD_HOVER_BACKGROUND = new Color(210, 210, 230);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 24);
    private static final int PADDING = 20;

    public AdminDashboard(User user, AdminController adminController, CardLayout cardLayout, JPanel cardPanel) {
        this.user = user;
        this.adminController = adminController;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
//        this.adminMenuController = new AdminMenuController(this.user, adminController, cardLayout, cardPanel);

        setLayout(new BorderLayout());

        // Add the header
        JPanel header =(new Header("Imagine Library", user.getName(), this::handleLogout));

        add(header, BorderLayout.NORTH);

        // Create the main content panel
//        JPanel mainContent = new JPanel(new BorderLayout());

        // Add the sidebar (menu)
//        String[] menuItems = {"Home", "View Profile", "Manage Books", "Manage Users", "Borrowed History"};
//        JPanel sideBar = new JPanel();
//        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
//        sideBar.add(new Menu(menuItems, adminMenuController::handleMenuButtonClick));
//        add(sideBar,BorderLayout.WEST);
//        mainContent.add(sideBar, BorderLayout.WEST);
        JPanel sideBar=createSidebar();
        add(sideBar,BorderLayout.WEST);
        innerCardLayout =new CardLayout();
        innerCardPanel=new JPanel(innerCardLayout);
        add(innerCardPanel,BorderLayout.CENTER);
        initializeMainContentPanels();




//        mainContent.add(dynamicContentPanel, BorderLayout.CENTER); // Use dynamicContentPanel instead of cardPanel
//        add(mainContent, BorderLayout.CENTER);

        // Initialize the default view (AdminDashboard)
//        JPanel adminDashboardContent = createMainContent();
//        dynamicContentPanel.add(adminDashboardContent); // Add content to dynamicContentPanel

    }
    private void initializeMainContentPanels() {
        // Add panels to the main content area
        innerCardPanel.add(createHomePanel(), "Home");
        innerCardPanel.add(createManageUsersPanel(), "ManageUsers");
//        innerCardPanel.add(createManageBooksPanel(), "ManageBooks");

        // Show the default panel
        innerCardLayout.show(innerCardPanel, "Home");
    }
    private JPanel createSidebar() {
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create the AdminMenuController with navigation callbacks
        AdminMenuController adminMenuController = new AdminMenuController(
                user,
                adminController,
                () -> innerCardLayout.show(innerCardPanel, "Home"), // Navigate to Home
                () -> innerCardLayout.show(innerCardPanel, "ManageBooks"), // Navigate to Manage Books
                () -> innerCardLayout.show(innerCardPanel, "ManageUsers"), // Navigate to Manage Users
                () -> innerCardLayout.show(innerCardPanel, "BorrowedHistory") // Navigate to Borrowed History
        );

        // Add buttons to the sidebar
        String[] menuItems = {"Home", "View Profile", "Manage Books", "Manage Users", "Borrowed History"};
        Menu menu =new Menu(menuItems,adminMenuController::handleMenuButtonClick);
        sideBar.add(menu);
        return sideBar;
    }


    private void handleLogout() {
        cardLayout.show(cardPanel, "Login");
        JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE); // Show logout message
    }
    private JPanel createManageUsersPanel(){
        return new ManageUsersPanel(adminController);
    }

    private JPanel createHomePanel() {
        // Load icons (replace with your actual image paths)
        ImageIcon usersIcon = scaleIcon("src/images/icons/users.png",50,50);
        ImageIcon booksIcon = scaleIcon("src/images/icons/books.png",50,50);
        ImageIcon genresIcon = scaleIcon("src/images/icons/books.png",50,50);
        ImageIcon availableBooksIcon = scaleIcon("src/images/icons/avialableBooks.png",50,50);
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)); // Padding

        // Create a panel for the cards (top part)
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 2x2 grid layout
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Add bottom margin

        // Fetch data from the AdminController
        int totalUsers = adminController.totalUsers();
        int totalBooks = adminController.totalBooks();
        int totalGenre = adminController.totalGenre();
        int totalAvailableBooks = adminController.totalAvailableBooks();

        // Create statistical cards with click actions
        statsPanel.add((createStatCard("Total Users", String.valueOf(totalUsers), usersIcon, () -> {
            cardLayout.show(cardPanel, "UserManagement");
        })));

        statsPanel.add((createStatCard("Total Books", String.valueOf(totalBooks), booksIcon, () -> {
            cardLayout.show(cardPanel, "BookManagement");
        })));

        statsPanel.add((createStatCard("Total Genres", String.valueOf(totalGenre), genresIcon, () -> {
            cardLayout.show(cardPanel, "GenreManagement");
        })));

        statsPanel.add((createStatCard("Total Available Books", String.valueOf(totalAvailableBooks), availableBooksIcon, () -> {
            cardLayout.show(cardPanel, "BooksManagement");
        })));

        mainContent.add(statsPanel, BorderLayout.NORTH);

        // Fetch user and book data from the AdminController
        List<User> users = adminController.allUsers();
        List<Book> books = adminController.allBooks();

        // Create the user table panel
        String[] userColumns = {"Name", "Email", "Role"};
        JPanel userTablePanel = createTablePanel("Users", users, userColumns, () -> {
            cardLayout.show(cardPanel, "UserManagement");
        }, () -> {
            cardLayout.show(cardPanel, "UserManagement");
        });

        // Create the book table panel
        String[] bookColumns = {"ID", "Title", "Author", "Genre"};
        JPanel bookTablePanel = createTablePanel("Books", books, bookColumns, () -> {
            cardLayout.show(cardPanel, "BookManagement");
        }, () -> {
            cardLayout.show(cardPanel, "BookManagement");
        });

        // Create a panel for the tables (bottom part)
        JPanel tablePanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 1 row, 2 columns for user and book tables
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add top margin

        // Add the table panels to the table panel
        tablePanel.add(userTablePanel);
        tablePanel.add(bookTablePanel);

        // Add the table panel to the main content (bottom)
        mainContent.add(tablePanel, BorderLayout.CENTER);

        return mainContent;
    }



    private JPanel createTablePanel(String title, List<?> data, String[] columns, Runnable seeAllAction, Runnable showLessAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Create the title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);


        // Add only the first 10 items to the table initially
        int limit = Math.min(10, data.size()); // Display up to 10 items
        for (int i = 0; i < limit; i++) {
            Object item = data.get(i);
            if (item instanceof User) {
                User user = (User) item;
                tableModel.addRow(new Object[]{user.getName(), user.getEmail(), user.getRole()});
            } else if (item instanceof Book) {
                Book book = (Book) item;
                tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre()});
            }
        }

        // Create the table
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);


        // Create the "See All" button
        JButton seeAllButton = new JButton("See All");

        // Create the "Show Less" button
        JButton showLessButton = new JButton("Show Less");
        showLessButton.setVisible(false);

        // Add action listeners
        seeAllButton.addActionListener(e -> {

            tableModel.setRowCount(0);
            for (Object item : data) {
                if (item instanceof User) {
                    User user = (User) item;
                    tableModel.addRow(new Object[]{user.getName(), user.getEmail(), user.getRole()});
                } else if (item instanceof Book) {
                    Book book = (Book) item;
                    tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre()});
                }
            }
            seeAllButton.setVisible(false);
            showLessButton.setVisible(true);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.revalidate();
        });

        showLessButton.addActionListener(e -> {
            // Load only the first 10 items into the table
            tableModel.setRowCount(0); // Clear existing rows
            int newLimit = Math.min(10, data.size()); // Display up to 10 items
            for (int i = 0; i < newLimit; i++) {
                Object item = data.get(i);
                if (item instanceof User) {
                    User user = (User) item;
                    tableModel.addRow(new Object[]{user.getName(), user.getEmail(), user.getRole()});
                } else if (item instanceof Book) {
                    Book book = (Book) item;
                    tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre()});
                }
            }
            showLessButton.setVisible(false);
            seeAllButton.setVisible(true);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.revalidate();
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(seeAllButton);
        buttonPanel.add(showLessButton);

        // Create a panel for the title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Add components to the main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    private ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage(); // Get the image from the icon
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Resize image
        return new ImageIcon(scaledImage); // Return the scaled image as ImageIcon
    }

    private JPanel createStatCard(String title, String value, ImageIcon icon, Runnable onClickAction) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Vertical layout
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor to hand when hovering
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the icon horizontally

        // Create the title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title horizontally

        // Create the value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(VALUE_FONT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the value horizontally

        // Add components to the card
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10)); // Add spacing between icon and title
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10)); // Add spacing between title and value
        card.add(valueLabel);

        // Add MouseListener for hover and click effects
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClickAction.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER_BACKGROUND); // Change background on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BACKGROUND); // Restore original background
            }
        });

        return card;
    }


}
