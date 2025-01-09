package services;

import models.Book;
import utils.CSVUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private static final String BOOKS_CSV = "D:\\OneDrive - itc.edu.kh\\teamProject\\IdeaProjects\\libraryProject\\src\\data\\booksInfo.csv";
    private static final int ID_INDEX = 0;
    private static final int ISBN_INDEX = 1;
    private static final int TITLE_INDEX = 2;
    private static final int GENRE_INDEX = 3;
    private static final int AUTHOR_INDEX = 4;
    private static final int IMG_PATH_INDEX = 5;
    private static final int LINK_INDEX = 6;
    private static final int IS_AVAILABLE_INDEX = 7;

    private Map<String, Book> bookMap; // HashMap for fast lookup (id as key)
    private List<Book> bookList;      // ArrayList to store books in order
    private int nextId;               // Track the next available ID

    public BookService() {
        this.bookMap = new HashMap<>();
        this.bookList = new ArrayList<>();
        this.nextId = 1; // Start IDs from 1
        loadBooks(); // Load books from CSV file when the service is initialized
    }

    // Load books from CSV file
    public void loadBooks() {
        List<String[]> rows = CSVUtils.readCSV(BOOKS_CSV);
        for (String[] row : rows) {
            if (row.length >= 8) {
                Book book = new Book(
                        row[ID_INDEX],
                        row[ISBN_INDEX],
                        row[TITLE_INDEX],
                        row[GENRE_INDEX],
                        row[AUTHOR_INDEX],
                        row[IMG_PATH_INDEX],
                        row[LINK_INDEX],
                        Boolean.parseBoolean(row[IS_AVAILABLE_INDEX])
                );
                bookMap.put(book.getId(), book); // Add to HashMap
                bookList.add(book); // Add to ArrayList

                // Update nextId to ensure it's always greater than the highest existing ID
                int bookId = Integer.parseInt(book.getId());
                if (bookId >= nextId) {
                    nextId = bookId + 1;
                }

                System.out.println("Loaded book: " + book.getTitle());
            } else {
                System.err.println("Invalid row in CSV: " + String.join(",", row));
            }
        }
    }

    // Add a new book with auto-generated ID
    public boolean addBook(Book newBook) {
        if (newBook == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        // Generate a new ID for the book
        String newId = String.valueOf(nextId);
        newBook.setId(newId); // Set the auto-generated ID

        if (bookMap.containsKey(newId)) {
            return false; // Book already exists (should not happen with auto-generated IDs)
        }
        bookMap.put(newId, newBook);
        bookList.add(newBook);
        nextId++; // Increment the next available ID
        saveBooks(); // Save to CSV
        return true;
    }

    // Save books to CSV file
    public void saveBooks() {
        List<String[]> data = new ArrayList<>();
        for (Book book : bookList) {
            data.add(new String[]{
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getGenre(),
                    book.getAuthor(),
                    book.getImgPath(),
                    book.getLink(),
                    String.valueOf(book.isAvailable())
            });
        }

        // Convert List<String[]> to List<String> for CSVUtils.writeCSV
        List<String> csvLines = new ArrayList<>();
        for (String[] row : data) {
            csvLines.add(String.join(",", row)); // Convert each row to a CSV line
        }

        // Write to CSV
        CSVUtils.writeCSV(BOOKS_CSV, csvLines);
    }
    // Get all books
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookList); // Return a copy to prevent external modification
    }

//    // Example usage
//    public static void main(String[] args) {
//        BookService bookService = new BookService();
//        System.out.println("Books loaded: " + bookService.getAllBooks().size());
//
//        // Add a new book (ID will be auto-generated)
//        Book newBook = new Book(
//                "978-0-06-112008-4",
//                "Animal Farm",
//                "Dystopian",
//                "George Orwell",
//                "images/animal_farm.jpg",
//                "http://example.com/animal_farm",
//                true
//        );
//        if (bookService.addBook(newBook)) {
//            System.out.println("Book added successfully! ID: " + newBook.getId());
//        } else {
//            System.out.println("Book already exists!");
//        }
//
//        // Retrieve all books
//        for (Book book : bookService.getAllBooks()) {
//            System.out.println(book);
//        }
//    }
}