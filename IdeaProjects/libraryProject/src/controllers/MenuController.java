package controllers;

import models.User;
import ui.components.UserProfileDialog;

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
        System.out.println(user.getImgPath());
        System.out.println("MenuController initialized with user: " +user.getRole()); // Debugging statement
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
        UserProfileDialog profileDialog =new UserProfileDialog(user);
        profileDialog.setVisible(true);

    }

}