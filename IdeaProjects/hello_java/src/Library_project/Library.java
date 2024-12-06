package Library_project;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Library {

    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private User currentUser = null;
    private Admin admin = new Admin();
    private ArrayList<Member> members = new ArrayList<>();
    static Scanner input = new Scanner(System.in);
    private static final String BOOKS_FILE_PATH = "Books.csv";
    private static final String REGISTER_FILE_PATH = "register.csv";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_MAGENTA = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";

    public void mainMenu(){
        System.out.println(ANSI_MAGENTA+"\t\t\t======== >Hello welcome to West side Library <======="+ANSI_RESET);
        System.out.println("Please enter to continue...");
        input.nextLine();
        int choice=0;
        do{
            System.out.println("1.Admin");
            System.out.println("2.User");
            System.out.println("3.Quit");
            System.out.print("Enter your choice: ");
            if(!input.hasNextInt()){
                System.out.println(ANSI_RED+"Sorry invalid choice!!please enter a number"+ANSI_RESET);
                input.next();
                continue;

            }
             choice=input.nextInt();

            switch (choice){
                case 1:
                    loginAdmin();
                    break;
                case 2:
                    userMenu();
                    break;
                case 3:
                    System.out.println("Thank for visiting,Have a nice day!!!!");
            }
        }while (choice!=3);


    }
    public void adminMenu(){

        System.out.println("=======> Welcome to Admin Menu <=========");
        System.out.println("1.View all books");
        System.out.println("2.View all users registered.");
        System.out.println("3.Back to previous page");
        System.out.print("Enter your choice: ");
        if (!input.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            input.next(); // clear invalid input
        }
        int choice = input.nextInt();
        switch (choice){
            case 1:
                loadBooksFromCsv();
                break;
            case 2:
                loadUsers();
                break;
            case 3:
                userMenu();
                break;
            default:
                System.out.println("Please enter again");
        }
    }
    public boolean loginAdmin(){
        input.nextLine();
        System.out.print("Enter your ID: ");
        String id=input.nextLine();
        System.out.print("Enter user name: ");
        String name=input.nextLine();
        System.out.print("Enter password: ");
        String password=input.nextLine();
        if (admin.verifyAdmin(name,id,password)){
            System.out.println("Admin login successful.");
            adminMenu();
            return true;
        }
        System.out.println("Sorry invalid input.");
        return false;
    }
    public boolean loginUser(){
        input.nextLine();
        System.out.print("Enter your ID: ");
        String id = input.nextLine();
        System.out.print("Enter your password: ");
        String password = input.nextLine();
        try(BufferedReader reader = new BufferedReader(new FileReader(REGISTER_FILE_PATH))) {
            String line;
            while ((line =reader.readLine())!=null){
                String[] parts=line.split(",");
                if(parts.length==3){
                    String fileId=parts[0].trim();
                    String filePassword=parts[2].trim();
                    if(fileId.equals(id) && filePassword.equals(password)){
                        System.out.println("Login successful!");
                        return  true;
                    }
                }

            }
            System.out.println("Invalid password");
            System.out.println("Please try again");
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void registerUser(){
        input.nextLine();
        System.out.print("Enter your ID: ");
        String id =input.nextLine();
        System.out.print("Enter your name: ");
        String name=input.nextLine();
        System.out.print("Enter your password: ");
        String password=input.nextLine();
        Member member =new Member(id,name,password);
        users.add(member);
//                System.out.println("Confirm your password: ");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(REGISTER_FILE_PATH, true))) {
            writer.write(id+","+name+","+password);
            writer.newLine();
            System.out.println("Registration successfully");

        } catch (IOException e) {
            System.out.println("Something went wrong: "+e.getMessage());
        }

    }
    public void userMenu()  {
        String name;
        String id;
        String password;
        String confirmPassword;

        System.out.println("1.Login.");
        System.out.println("2.Register.");
        System.out.println("3.Back to previous menu.");
        System.out.print("Please enter your choice: ");
        int choice =input.nextInt();
        switch (choice){
            case 1:
                loginUser();
                break;
            case 2:
                registerUser();
                break;
            case 3:
                mainMenu();
                break;
            default:
                System.out.println("Please input again");
        }

    }
    public void loadUsers(){
        // try with resource no need final
        try(BufferedReader reader = new BufferedReader(new FileReader(REGISTER_FILE_PATH))) {
            String line;
            reader.readLine();
            while((line=reader.readLine())!=null){
                String[] details=line.split(",");
                if(details.length==3){
                    String id = details[0].trim();
                    String userName = details[1].trim();
                    String password = details[0].trim();
                    members.add(new Member(id,userName,password));
                }else{
                    System.out.println(ANSI_RED+"Invalid book entry "+line+ANSI_RESET);
                }
            }

        }catch (IOException e){
            System.out.println(ANSI_RED+"Something went wrong:"+e.getMessage());
        }
        displayRegistration();

    }
    public void displayRegistration(){
        if(users.isEmpty()){
            System.out.println("No user registered in the list");
        }
        for(Member member:members){
            System.out.println(ANSI_BLUE+member.toString()+ANSI_RESET);
        }

    }
    public void loadBooksFromCsv(){
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))){
            String line;
            reader.readLine();
            while ((line=reader.readLine())!=null){
                String[] details=line.split(",");
                if(details.length==4) {
                    String isbn=details[0].trim();
                    String tittle=details[1].trim();
                    String author=details[2].trim();
                    String genre=details[3].trim();
                    books.add(new Book(isbn,tittle,author,genre));
                }else {
                    System.out.println("Invalid book entry: "+line);
                }
            }
            System.out.println("Books loaded successfully");
        }catch (IOException e) {
            System.out.println(ANSI_RED+"Something went wrong:"+e.getMessage());
        }
        displayBooks();
    }
    public void displayBooks(){
        if (books.isEmpty()){
            System.out.println("No books available in the library");
        }else{
            for(Book book:books){
                System.out.println(ANSI_BLUE+book.toString()+ANSI_RESET);
            }
        }
    }

}
