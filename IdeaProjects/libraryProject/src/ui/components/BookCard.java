package ui.components;

import models.Book;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static utils.ImageLoader.loadImageIcon;

public class BookCard extends JPanel {
    public BookCard(Book book, Runnable readAction, Runnable viewDetailsAction) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Vertical layout
        setPreferredSize(new Dimension(180, 260)); // Set card size
        setBackground(Color.WHITE); // White background
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1), // Light gray border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding inside the card
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover

        // Load the book image
        ImageIcon originalIcon = loadImageIcon(book.getImgPath(), 120, 160); // Load and resize image
        JLabel bookImage = new JLabel(originalIcon);
        bookImage.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the image

        // Create and style the title label
        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font for title
        titleLabel.setForeground(new Color(50, 50, 50)); // Dark gray text
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title

        // Create and style the author label
        JLabel authorLabel = new JLabel("By " + book.getAuthor());
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 12)); // Plain font for author
        authorLabel.setForeground(new Color(100, 100, 100)); // Medium gray text
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the author

        // Create and style the Read button
        JButton readButton = new JButton("Read");
        readButton.setFont(new Font("Arial", Font.PLAIN, 12)); // Font for button
        readButton.setBackground(new Color(90, 160, 255)); // Light blue background
        readButton.setForeground(Color.WHITE); // White text
        readButton.setFocusPainted(false); // Remove focus border
        readButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        readButton.addActionListener(e -> readAction.run()); // Action for Read button

        // Add components to the book card
        add(bookImage);
        add(Box.createVerticalStrut(10)); // Add vertical spacing
        add(titleLabel);
        add(Box.createVerticalStrut(5)); // Add vertical spacing
        add(authorLabel);
        add(Box.createVerticalStrut(10)); // Add vertical spacing
        add(readButton); // Add the Read button

        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                setBackground(new Color(245, 245, 245)); // Light gray background on hover
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Darker border on hover
                        BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding for shadow effect
                ));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                setBackground(Color.WHITE); // Reset background color
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Reset border
                        BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding for shadow effect
                ));
            }
        });

        // Add click listener to redirect to the book details screen
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Check if the source is a BookCard
                if (evt.getSource() instanceof BookCard) {
                    // If the click wasn't on the Read button inside the BookCard
                    if (evt.getComponent() != readButton) {
                        // Trigger the view details action
                        viewDetailsAction.run();
                    }
                }
            }
        });

    }
}
