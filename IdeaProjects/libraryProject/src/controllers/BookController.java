package controllers;

import models.Book;
import services.BookService;

import java.util.List;

public class BookController {
    private BookService bookService;

    public BookController() {
        bookService = new BookService();
    }

    // Method to add a new book
    public boolean addBook(String isbn, String title, String genre, String author, String imgPath, String link, boolean isAvailable) {
        Book newBook = new Book(isbn, title, genre, author, imgPath, link, isAvailable);
        return bookService.addBook(newBook);
    }

    // Method to retrieve all books
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // Method to find a book by ID
    public Book getBookById(String id) {
        return bookService.getAllBooks().stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Method to update a book's availability
    public boolean updateBookAvailability(String id, boolean isAvailable) {
        Book book = getBookById(id);
        if (book != null) {
            book.setAvailable(isAvailable);
            bookService.saveBooks(); // Save changes to CSV
            return true;
        }
        return false;
    }

    // Method to delete a book by ID
    public boolean deleteBook(String id) {
        Book book = getBookById(id);
        if (book != null) {
            bookService.getAllBooks().remove(book);
            bookService.saveBooks(); // Save changes to CSV
            return true;
        }
        return false;
    }
}
