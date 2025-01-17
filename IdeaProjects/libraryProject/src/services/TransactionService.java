package services;

import models.Transaction;
import utils.CSVUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionService {
    private static final String CSV_HEADER = "id,userEmail,bookId,borrowDate,dueDate,returnDate,isBorrow";
    private static final String TRANSACTION_CSV = "IdeaProjects/libraryProject/src/data/transaction.csv";
    private static final int ID_INDEX = 0;
    private static final int USER_EMAIL_INDEX = 1; // Updated to use email
    private static final int BOOK_ID_INDEX = 2;
    private static final int BORROW_DATE_INDEX = 3;
    private static final int DUE_DATE_INDEX = 4;
    private static final int RETURN_DATE_INDEX = 5;
    private static final int IS_BORROW_INDEX = 6; // New field for status

    private final Map<String, Transaction> transactionMap; // HashMap for fast lookup (id as key)
    private final List<Transaction> transactionList;      // ArrayList to store transactions in order
    private int nextId;                                   // Track the next available ID

    public TransactionService() {
        this.transactionMap = new HashMap<>();
        this.transactionList = new ArrayList<>();
        this.nextId = 1; // Start IDs from 1
        loadTransactions(); // Load transactions from CSV file when the service is initialized
    }

    /**
     * Load transactions from the CSV file into memory.
     */
    private void loadTransactions() {
        List<String[]> rows = CSVUtils.readCSV(TRANSACTION_CSV);
        for (String[] row : rows) {
            if (row.length >= 7) { // Ensure the row has all required fields
                try {
                    String transactionId = row[ID_INDEX];

                    // Check if a transaction with the same ID already exists
                    if (transactionMap.containsKey(transactionId)) {
                        System.err.println("Duplicate transaction ID found in CSV: " + transactionId);
                        continue; // Skip this row
                    }

                    LocalDate returnDate = row[RETURN_DATE_INDEX].isEmpty() ? null : LocalDate.parse(row[RETURN_DATE_INDEX]);
                    boolean isBorrow = Boolean.parseBoolean(row[IS_BORROW_INDEX]); // Parse the status field
                    Transaction transaction = new Transaction(
                            transactionId,
                            row[USER_EMAIL_INDEX], // Use email
                            row[BOOK_ID_INDEX],
                            LocalDate.parse(row[BORROW_DATE_INDEX]),
                            LocalDate.parse(row[DUE_DATE_INDEX]),
                            returnDate,
                            isBorrow
                    );

                    transactionMap.put(transaction.getId(), transaction); // Add to HashMap
                    transactionList.add(transaction); // Add to ArrayList

                    // Update nextId to ensure it's always greater than the highest existing ID
                    int idAsInt = Integer.parseInt(transactionId);
                    if (idAsInt >= nextId) {
                        nextId = idAsInt + 1; // Set nextId to the highest ID + 1
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + String.join(",", row));
                    e.printStackTrace();
                }
            } else {
                System.err.println("Invalid row in CSV: " + String.join(",", row));
            }
        }
    }

    /**
     * Borrow a book by creating a new transaction.
     *
     * @param userEmail The email of the user borrowing the book.
     * @param bookId    The ID of the book being borrowed.
     * @return True if the transaction was created successfully, false otherwise.
     */
    public boolean borrowBook(String userEmail, String bookId) {
        if (userEmail == null || userEmail.trim().isEmpty() || bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("User email and Book ID are required fields");
        }

        // Check if the user has already borrowed the same book and it's not returned
        boolean alreadyBorrowed = transactionList.stream()
                .anyMatch(t -> t.getUserEmail().equals(userEmail)
                        && t.getBookId().equals(bookId)
                        && t.isBorrow()
                        && t.getReturnDate() == null);

        if (alreadyBorrowed) {
            return false; // User has already borrowed this book
        }

        // Generate a new ID for the transaction
        String newId = String.valueOf(nextId);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusWeeks(2); // 2 weeks lending period

        Transaction transaction = new Transaction(newId, userEmail, bookId, borrowDate, dueDate, null, true); // isBorrow = true

        if (transactionMap.containsKey(newId)) {
            return false; // Transaction already exists (should not happen with auto-generated IDs)
        }

        transactionMap.put(newId, transaction);
        transactionList.add(transaction);
        nextId++; // Increment the next available ID
        saveTransactions(); // Save to CSV
        return true;
    }

    /**
     * Return a book by updating the corresponding transaction.
     *
     * @param userEmail The email of the user returning the book.
     * @param bookId    The ID of the book being returned.
     * @return True if the transaction was updated successfully, false otherwise.
     */
    public boolean returnBook(String userEmail, String bookId) {
        if (userEmail == null || userEmail.trim().isEmpty() || bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("User email and Book ID are required fields");
        }

        // Find the transaction for the user and book that hasn't been returned yet
        Optional<Transaction> optionalTransaction = transactionList.stream()
                .filter(t -> t.getUserEmail().equals(userEmail) && t.getBookId().equals(bookId) && t.isBorrow() && t.getReturnDate() == null)
                .findFirst();

        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setReturnDate(LocalDate.now()); // Set the return date
            transaction.setBorrow(false); // Mark as return
            saveTransactions(); // Save changes to CSV
            return true;
        }

        return false; // No matching transaction found
    }

    /**
     * Save all transactions to the CSV file.
     */
    public void saveTransactions() {
        List<String[]> data = transactionList.stream()
                .map(transaction -> new String[]{
                        transaction.getId(),
                        transaction.getUserEmail(), // Use email
                        transaction.getBookId(),
                        transaction.getBorrowDate().toString(),
                        transaction.getDueDate().toString(),
                        transaction.getReturnDate() != null ? transaction.getReturnDate().toString() : "",
                        String.valueOf(transaction.isBorrow()) // Include the status field
                })
                .collect(Collectors.toList());

        // Write to CSV
        CSVUtils.updateCSV(TRANSACTION_CSV, data,CSV_HEADER);
    }

    /**
     * Get all transactions.
     *
     * @return A list of all transactions.
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactionList); // Return a copy to prevent external modification
    }

    /**
     * Get a transaction by its ID.
     *
     * @param id The ID of the transaction.
     * @return The transaction with the specified ID, or null if not found.
     */
    public Transaction getTransactionById(String id) {
        return transactionMap.get(id);
    }

    /**
     * Get all transactions for a specific user.
     *
     * @param userEmail The email of the user.
     * @return A list of transactions for the specified user.
     */
    public List<Transaction> getTransactionsByUser(String userEmail) {
        return transactionList.stream()
                .filter(transaction -> transaction.getUserEmail().equals(userEmail))
                .collect(Collectors.toList());
    }

    /**
     * Get all transactions for a specific book.
     *
     * @param bookId The ID of the book.
     * @return A list of transactions for the specified book.
     */
    public List<Transaction> getTransactionsByBook(String bookId) {
        return transactionList.stream()
                .filter(transaction -> transaction.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }

    /**
     * Get all active (unreturned) transactions.
     *
     * @return A list of all active transactions.
     */
    public List<Transaction> getActiveTransactions() {
        return transactionList.stream()
                .filter(transaction -> transaction.isBorrow() && transaction.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    /**
     * Generate a unique transaction ID.
     *
     * @return A new unique ID.
     */
    public String generateId() {
        return "TXN-" + System.currentTimeMillis();
    }
}