import java.io.*;
import java.time.LocalDate;
import java.util.*;

// Book class
class Book implements Serializable {
    private String title;
    private String author;
    private boolean isIssued;
    private LocalDate dueDate;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isIssued = false;
        this.dueDate = null;
    }

    public void issueBook() {
        isIssued = true;
        dueDate = LocalDate.now().plusDays(14); // 2-week loan
    }

    public void returnBook() {
        isIssued = false;
        dueDate = null;
    }

    public long calculateFine() {
        if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now()) * 5; // ₹5/day fine
        }
        return 0;
    }

    public String getTitle() {
        return title;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return " Title: " + title +
                "\n Author: " + author +
                "\n Status: " + (isIssued ? "Issued (Due: " + dueDate + ")" : "Available") +
                "\n---------------------------";
    }
}

// Library class
class Library {
    private List<Book> books;
    private final String FILE_NAME = "library.dat";

    public Library() {
        books = loadBooks();
    }

    public void addBook(String title, String author) {
        books.add(new Book(title, author));
        saveBooks();
        System.out.println(" Book added successfully.");
    }

    public void viewBooks() {
        if (books.isEmpty()) {
            System.out.println(" No books in the library.");
            return;
        }
        for (Book b : books) {
            System.out.println(b);
        }
    }

    public void searchBook(String keyword) {
        boolean found = false;
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) {
            System.out.println(" No matching books found.");
        }
    }

    public void issueBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title) && !b.isIssued()) {
                b.issueBook();
                System.out.println(" Book issued. Due date: " + b.getDueDate());
                saveBooks();
                return;
            }
        }
        System.out.println("Book not available or already issued.");
    }

    public void returnBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title) && b.isIssued()) {
                long fine = b.calculateFine();
                b.returnBook();
                System.out.println(" Book returned.");
                if (fine > 0) {
                    System.out.println(" Fine: ₹" + fine);
                } else {
                    System.out.println("No fine.");
                }
                saveBooks();
                return;
            }
        }
        System.out.println(" Book not found or not issued.");
    }

    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(books);
        } catch (IOException e) {
            System.out.println(" Error saving books.");
        }
    }

    private List<Book> loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Book>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n --- Library Menu ---");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String author = sc.nextLine();
                    lib.addBook(title, author);
                    break;
                case 2:
                    lib.viewBooks();
                    break;
                case 3:
                    System.out.print("Enter keyword to search: ");
                    String keyword = sc.nextLine();
                    lib.searchBook(keyword);
                    break;
                case 4:
                    System.out.print("Enter title to issue: ");
                    lib.issueBook(sc.nextLine());
                    break;
                case 5:
                    System.out.print("Enter title to return: ");
                    lib.returnBook(sc.nextLine());
                    break;
                case 6:
                    System.out.println(" Exiting... Goodbye!");
                    break;
                default:
                    System.out.println(" Invalid choice.");
            }
        } while (choice != 6);
    }
}