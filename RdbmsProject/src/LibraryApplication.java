import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class LibraryApplication {
    private static final String DB_URL = "jdbc:mysql://localhost/library";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private static final int STANDARD_MEMBER = 1;
    private static final int STAFF_MEMBER = 2;
    private static final int SENIOR_CITIZEN_MEMBER = 3;
    private static void createTables(Connection connection) throws SQLException {

        String createMembersTableQuery = "CREATE TABLE IF NOT EXISTS members (" +
                "member_id INT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "address VARCHAR(255)," +
                "phone_number VARCHAR(20)," +
                "membership_type INT," +
                "credit_card_number VARCHAR(16)," +
                "credit_card_cvv VARCHAR(4)," +
                "credit_card_exp_date VARCHAR(7)" +
                ")";

        String createBooksTableQuery = "CREATE TABLE IF NOT EXISTS books (" +
                "book_id INT PRIMARY KEY," +
                "title VARCHAR(255)," +
                "author VARCHAR(255)," +
                "category VARCHAR(255)," +
                "isbn VARCHAR(20) UNIQUE" +
                ")";

//        String createBorrowedBooksTableQuery = "CREATE TABLE IF NOT EXISTS borrowed_books (" +
//                "borrow_id INT PRIMARY KEY AUTO_INCREMENT," +
//                "member_id INT," +
//                "book_id INT," +
//                "borrow_date DATE," +
//                "return_date DATE," +
//                "late_fee DECIMAL(10,2)," +
//                "status VARCHAR(255)" +
//                ")";

        String createBorrowedBooksTableQuery = "CREATE TABLE IF NOT EXISTS borrowed_books (" +
                "borrow_id INT PRIMARY KEY AUTO_INCREMENT," +
                "member_id INT," +
                "book_id INT," +
                "borrow_date DATE," +
                "return_date DATE," +
                "actual_return_date DATE," +
                "late_fee DECIMAL(10, 2)," +
                "status VARCHAR(255)" +
                ")";

        String createPublishersTableQuery = "CREATE TABLE IF NOT EXISTS publishers (" +
                "publisher_id INT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "head_office_address VARCHAR(255)," +
                "main_telephone_number VARCHAR(20)" +
                ")";

        String createBookCopiesTableQuery = "CREATE TABLE IF NOT EXISTS book_copies (" +
                "copy_id INT PRIMARY KEY," +
                "book_id INT," +
                "is_available BOOLEAN" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createMembersTableQuery);
            statement.executeUpdate(createBooksTableQuery);
            statement.executeUpdate(createBorrowedBooksTableQuery);
            statement.executeUpdate(createBookCopiesTableQuery);
            statement.executeUpdate(createPublishersTableQuery);
            System.out.println("Tables created successfully.");
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            createTables(connection);
            System.out.println("Connected to the database");


            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Display menu options
                System.out.println("Library System Menu:");
                System.out.println("1. Add Member");
                System.out.println("2. Update Member");
                System.out.println("3. Remove Member");
                System.out.println("4. Search Members");
                System.out.println("5. Add Book");
                System.out.println("6. Search Books");
                System.out.println("7. Check Out Book");
                System.out.println("8. Check In Book");
                System.out.println("9. Show List of Checked-Out Books");
                System.out.println("10. Show List of Books Borrowed by Member");
                System.out.println("11. Show Overdue Members and Borrowed Books");
                System.out.println("12. Show Amount of Overdue Fees");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 0:
                        // Exit the application
                        System.out.println("Exiting the application");
                        scanner.close();
                        connection.close();
                        System.exit(0);
                        break;
                    case 1:
                        // Add Member
                        addMember(connection, scanner);
                        break;
                    case 2:
                        // Update Member
                        updateMember(connection, scanner);
                        break;
                    case 3:
                        // Remove Member
                        removeMember(connection, scanner);
                        break;
                    case 4:
                        // Search Members
                        searchMembers(connection, scanner);
                        break;
                    case 5:
                        // Add Book
                        addBook(connection, scanner);
                        break;
                    case 6:
                        // Search Books
                        searchBooks(connection, scanner);
                        break;
                    case 7:
                        // Check Out Book
                        checkOutBook(connection, scanner);
                        break;
                    case 8:
                        // Check In Book
                        checkInBook(connection, scanner);
                        break;
                    case 9:
                        // Show List of Checked-Out Books
                        showCheckedOutBooks(connection);
                        break;
                    case 10:
                        // Show List of Books Borrowed by Member
                        showBorrowedBooksByMember(connection, scanner);
                        break;
                    case 11:
                        // Show Overdue Members and Borrowed Books
                        showOverdueMembersAndBooks(connection);
                        break;
                    case 12:
                        // Show Amount of Overdue Fees
                        showOverdueFees(connection);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void addMember(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID: ");
        int memberId = scanner.nextInt();
        System.out.println("Enter Name: ");
        String name = scanner.next();
        System.out.println("Enter Address: ");
        String address = scanner.next();
        System.out.println("Enter Phone Number: ");
        String phoneNumber = scanner.next();
        System.out.println("Enter Membership Type (1: Standard, 2: Staff, 3: Senior Citizen): ");
        int membershipType = scanner.nextInt();
        System.out.println("Enter Credit Card Number: ");
        String creditCardNumber = scanner.next();
        System.out.println("Enter Credit Card CVV: ");
        String creditCardCVV = scanner.next();
        System.out.println("Enter Credit Card Expiration Date (MM/YYYY): ");
        String creditCardExpDate = scanner.next();

        String insertMemberQuery = "INSERT INTO members (member_id, name, address, phone_number, membership_type, credit_card_number, credit_card_cvv, credit_card_exp_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertMemberQuery)) {
            statement.setInt(1, memberId);
            statement.setString(2, name);
            statement.setString(3, address);
            statement.setString(4, phoneNumber);
            statement.setInt(5, membershipType);
            statement.setString(6, creditCardNumber);
            statement.setString(7, creditCardCVV);
            statement.setString(8, creditCardExpDate);
            statement.executeUpdate();
            System.out.println("Member added successfully.");
        }
    }
    // old version of update member v1:
    /*private static void updateMember(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID to update: ");
        int memberId = scanner.nextInt();
        System.out.println("Enter New Address: ");
        String newAddress = scanner.next();

        String updateMemberQuery = "UPDATE members SET address = ? WHERE member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateMemberQuery)) {
            statement.setString(1, newAddress);
            statement.setInt(2, memberId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member updated successfully.");
            } else {
                System.out.println("Member not found.");
            }
        }
    }*/
    // new version of update member v2:
    private static void updateMember(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID to update: ");
        int memberId = scanner.nextInt();
        System.out.println("Enter New Name: ");
        String newName = scanner.next();
        System.out.println("Enter New Address: ");
        String newAddress = scanner.next();
        System.out.println("Enter New Phone Number: ");
        String newPhoneNumber = scanner.next();
        System.out.println("Enter New Membership Type (1: Standard, 2: Staff, 3: Senior Citizen): ");
        int newMembershipType = scanner.nextInt();
        System.out.println("Enter New Credit Card Number: ");
        String newCreditCardNumber = scanner.next();
        System.out.println("Enter New Credit Card CVV: ");
        String newCreditCardCVV = scanner.next();
        System.out.println("Enter New Credit Card Expiration Date (MM/YYYY): ");
        String newCreditCardExpDate = scanner.next();

        String updateMemberQuery = "UPDATE members SET name = ?, address = ?, phone_number = ?, membership_type = ?, " +
                "credit_card_number = ?, credit_card_cvv = ?, credit_card_exp_date = ? WHERE member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateMemberQuery)) {
            statement.setString(1, newName);
            statement.setString(2, newAddress);
            statement.setString(3, newPhoneNumber);
            statement.setInt(4, newMembershipType);
            statement.setString(5, newCreditCardNumber);
            statement.setString(6, newCreditCardCVV);
            statement.setString(7, newCreditCardExpDate);
            statement.setInt(8, memberId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member updated successfully.");
            } else {
                System.out.println("Member not found.");
            }
        }
    }

    private static void removeMember(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID to remove: ");
        int memberId = scanner.nextInt();

        String removeMemberQuery = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(removeMemberQuery)) {
            statement.setInt(1, memberId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member removed successfully.");
            } else {
                System.out.println("Member not found.");
            }
        }
    }
//************************************************
    private static void searchMembers(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Name or Phone Number to search: ");
        String searchQuery = scanner.next();

        String searchMembersQuery = "SELECT * FROM members WHERE name LIKE ? OR phone_number LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(searchMembersQuery)) {
            statement.setString(1, "%" + searchQuery + "%");
            statement.setString(2, "%" + searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int memberId = resultSet.getInt("member_id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String phoneNumber = resultSet.getString("phone_number");
                int membershipType = resultSet.getInt("membership_type");

                System.out.println("Member ID: " + memberId);
                System.out.println("Name: " + name);
                System.out.println("Address: " + address);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Membership Type: " + membershipType);
                System.out.println();
            }
        }
    }

    //old version of addBook v1:
   /* private static void addBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Book ID: ");
        int bookId = scanner.nextInt();
        System.out.println("Enter Title: ");
        String title = scanner.next();
        System.out.println("Enter Author: ");
        String author = scanner.next();
        System.out.println("Enter Category: ");
        String category = scanner.next();
        System.out.println("Enter ISBN: ");
        String isbn = scanner.next();

        String insertBookQuery = "INSERT INTO books (book_id, title, author, category, isbn) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertBookQuery)) {
            statement.setInt(1, bookId);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, category);
            statement.setString(5, isbn);
            statement.executeUpdate();
            System.out.println("Book added successfully.");
        }
    }*/
    //new version of addBook v2 (including the publisher info):
    private static void addBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Book ID: ");
        int bookId = scanner.nextInt();
        System.out.println("Enter Title: ");
        String title = scanner.next();
        System.out.println("Enter Author: ");
        String author = scanner.next();
        System.out.println("Enter Category: ");
        String category = scanner.next();
        System.out.println("Enter ISBN: ");
        String isbn = scanner.next();
        System.out.println("Enter Publisher ID: ");
        int publisherId = scanner.nextInt();
        System.out.println("Enter Publisher Name: ");
        String publisherName = scanner.next();
        System.out.println("Enter Publisher Head Office Address: ");
        String headOfficeAddress = scanner.next();
        System.out.println("Enter Publisher Main Telephone Number: ");
        String mainTelephoneNumber = scanner.next();

        // Insert book into books table
        String insertBookQuery = "INSERT INTO books (book_id, title, author, category, isbn) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertBookQuery)) {
            statement.setInt(1, bookId);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, category);
            statement.setString(5, isbn);
            statement.executeUpdate();
            System.out.println("Book added successfully.");
        }

        // Insert publisher into publishers table
        String insertPublisherQuery = "INSERT INTO publishers (publisher_id, name, head_office_address, main_telephone_number) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertPublisherQuery)) {
            statement.setInt(1, publisherId);
            statement.setString(2, publisherName);
            statement.setString(3, headOfficeAddress);
            statement.setString(4, mainTelephoneNumber);
            statement.executeUpdate();
            System.out.println("Publisher added successfully.");
        }
    }


    private static void searchBooks(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Search Keyword: ");
        String searchQuery = scanner.next();

        String searchBooksQuery = "SELECT * FROM books WHERE book_id LIKE ? OR title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(searchBooksQuery)) {
            statement.setString(1, "%" + searchQuery + "%");
            statement.setString(2, "%" + searchQuery + "%");
            statement.setString(3, "%" + searchQuery + "%");
            statement.setString(4, "%" + searchQuery + "%");
            statement.setString(5, "%" + searchQuery + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String category = resultSet.getString("category");
                String isbn = resultSet.getString("isbn");

                System.out.println("Book ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Category: " + category);
                System.out.println("ISBN: " + isbn);
                System.out.println();
            }
        }
    }


    private static void checkOutBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID: ");
        int memberId = scanner.nextInt();
        System.out.println("Enter Book ID: ");
        int bookId = scanner.nextInt();

        String checkOutBookQuery = "INSERT INTO borrowed_books (member_id, book_id, borrow_date, late_fee, status) VALUES (?, ?, CURDATE(), 0.0, 'Not Returned')";
        try (PreparedStatement statement = connection.prepareStatement(checkOutBookQuery)) {
            statement.setInt(1, memberId);
            statement.setInt(2, bookId);
            statement.executeUpdate();
            System.out.println("Book checked out successfully.");
        }

//    String checkOutBookQuery = "INSERT INTO borrowed_books (member_id, book_id, borrow_date) VALUES (?, ?, CURDATE())";
//    try (PreparedStatement statement = connection.prepareStatement(checkOutBookQuery)) {
//        statement.setInt(1, memberId);
//        statement.setInt(2, bookId);
//        statement.executeUpdate();
//        System.out.println("Book checked out successfully.");
//    }

        // Calculate the due date based on the membership type
        int membershipType = getMembershipType(connection, memberId);
        LocalDate dueDate = calculateDueDate(LocalDate.now(), membershipType);

        // Update the due_date in the borrowed_books table
        String updateDueDateQuery = "UPDATE borrowed_books SET return_date = ? WHERE member_id = ? AND book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateDueDateQuery)) {
            statement.setDate(1, java.sql.Date.valueOf(dueDate));
            statement.setInt(2, memberId);
            statement.setInt(3, bookId);
            statement.executeUpdate();
        }
    }

    private static int getMembershipType(Connection connection, int memberId) throws SQLException {
        String getMembershipTypeQuery = "SELECT membership_type FROM members WHERE member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(getMembershipTypeQuery)) {
            statement.setInt(1, memberId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("membership_type");
                } else {
                    throw new IllegalArgumentException("Invalid member ID");
                }
            }
        }
    }


    // Helper method to calculate the due date based on the current date and membership type
    private static LocalDate calculateDueDate(LocalDate currentDate, int membershipType) {
        int daysToBorrow;
        if (membershipType == STANDARD_MEMBER) {
            daysToBorrow = 21;
        } else if (membershipType == STAFF_MEMBER) {
            daysToBorrow = 21;
        } else if (membershipType == SENIOR_CITIZEN_MEMBER) {
            daysToBorrow = 42;
        } else {
            throw new IllegalArgumentException("Invalid membership type");
        }
        return currentDate.plusDays(daysToBorrow);
    }

    private static double calculateLateFee(int membershipType, int daysLate) {
        double lateFeePerDay = 0.0;
        if(daysLate > 0){
            if (membershipType == STANDARD_MEMBER) {
                lateFeePerDay = 0.5;
            } else if (membershipType == STAFF_MEMBER) {
                lateFeePerDay = 0.10;
            } else if (membershipType == SENIOR_CITIZEN_MEMBER) {
                lateFeePerDay = 0.05;
            } else {
                throw new IllegalArgumentException("Invalid membership type");
            }
        }

        return lateFeePerDay * daysLate;
    }

    //checkin v1
   /* private static void checkInBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID: ");
        int memberId = scanner.nextInt();
        System.out.println("Enter Book ID: ");
        int bookId = scanner.nextInt();

        // Retrieve the due date and membership type
        LocalDate dueDate = getDueDate(connection, memberId, bookId);
        int membershipType = getMembershipType(connection, memberId);

        // Calculate the late fee based on the number of days late
        LocalDate currentDate = LocalDate.now();
        int daysLate = (int) ChronoUnit.DAYS.between(dueDate, currentDate);
        double lateFee = calculateLateFee(membershipType, daysLate);

        // Update the late_fee and status columns in the borrowed_books table
        String updateCheckInQuery = "UPDATE borrowed_books SET late_fee = ?, status = 'Returned' WHERE member_id = ? AND book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateCheckInQuery)) {
            statement.setDouble(1, lateFee);
            statement.setInt(2, memberId);
            statement.setInt(3, bookId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Late Fee: $" + lateFee);
                System.out.println("Book checked in successfully.");
            } else {
                System.out.println("Book not found or not borrowed by the member.");
            }
        }
    }*/
    //checkin v2
    private static void checkInBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID: ");
        int memberId = scanner.nextInt();
        System.out.println("Enter Book ID: ");
        int bookId = scanner.nextInt();

        // Retrieve the due date and membership type
        LocalDate dueDate = getDueDate(connection, memberId, bookId);
        int membershipType = getMembershipType(connection, memberId);

        // Calculate the late fee and the difference in days
        LocalDate currentDate = LocalDate.now();
        int daysLate = (int) ChronoUnit.DAYS.between(dueDate, currentDate);
        double lateFee = calculateLateFee(membershipType, daysLate);

        // Update the actual_return_date, late_fee, and status columns in the borrowed_books table
        String updateCheckInQuery = "UPDATE borrowed_books SET actual_return_date = ?, late_fee = ?, status = 'Returned' WHERE member_id = ? AND book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateCheckInQuery)) {
            statement.setDate(1, java.sql.Date.valueOf(currentDate));
            statement.setDouble(2, lateFee);
            statement.setInt(3, memberId);
            statement.setInt(4, bookId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Late Fee: $" + lateFee);
                System.out.println("Book checked in successfully.");
            } else {
                System.out.println("Book not found or not borrowed by the member.");
            }
        }
    }


    private static LocalDate getDueDate(Connection connection, int memberId, int bookId) throws SQLException {
        String getDueDateQuery = "SELECT return_date FROM borrowed_books WHERE member_id = ? AND book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(getDueDateQuery)) {
            statement.setInt(1, memberId);
            statement.setInt(2, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDate("return_date").toLocalDate();
                } else {
                    throw new IllegalArgumentException("Book not found or not borrowed by the member.");
                }
            }
        }
    }

    private static void showCheckedOutBooks(Connection connection) throws SQLException {
        String checkedOutBooksQuery = "SELECT b.book_id, b.title, m.member_id, m.name FROM books b INNER JOIN borrowed_books bb ON b.book_id = bb.book_id INNER JOIN members m ON bb.member_id = m.member_id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(checkedOutBooksQuery)) {
            System.out.println("List of Checked-Out Books:");
            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                int memberId = resultSet.getInt("member_id");
                String memberName = resultSet.getString("name");

                System.out.println("Book ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println("Member ID: " + memberId);
                System.out.println("Member Name: " + memberName);
                System.out.println();
            }
        }
    }

    private static void showBorrowedBooksByMember(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter Member ID: ");
        int memberId = scanner.nextInt();

        String borrowedBooksQuery = "SELECT b.book_id, b.title FROM books b INNER JOIN borrowed_books bb ON b.book_id = bb.book_id WHERE bb.member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(borrowedBooksQuery)) {
            statement.setInt(1, memberId);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("List of Books Borrowed by Member:");
            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");

                System.out.println("Book ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println();
            }
        }
    }
    private static void showOverdueMembersAndBooks(Connection connection) throws SQLException {
        String overdueMembersAndBooksQuery = "" +
                "SELECT m.member_id, m.name, b.book_id, b.title " +
                "FROM members m INNER JOIN borrowed_books bb ON m.member_id = bb.member_id " +
                "INNER JOIN books b ON bb.book_id = b.book_id INNER JOIN book_copies bc ON b.book_id = bc.book_id " +
                "WHERE bb.return_date < CURRENT_DATE AND bc.is_available = false";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(overdueMembersAndBooksQuery)) {
            System.out.println("Overdue Members and Borrowed Books:");
            while (resultSet.next()) {
                int memberId = resultSet.getInt("member_id");
                String memberName = resultSet.getString("name");
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");

                System.out.println("Member ID: " + memberId);
                System.out.println("Member Name: " + memberName);
                System.out.println("Book ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println();
            }
        }
    }

    private static void showOverdueFees(Connection connection) throws SQLException {
        String overdueFeesQuery = "SELECT SUM(late_fee) AS total_overdue_fees FROM borrowed_books WHERE return_date < CURRENT_DATE";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(overdueFeesQuery)) {
            if (resultSet.next()) {
                double totalOverdueFees = resultSet.getDouble("total_overdue_fees");
                System.out.println("Amount of Overdue Fees: $" + totalOverdueFees);
            }
        }
    }
}
