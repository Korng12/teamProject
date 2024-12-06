package Library_project;

abstract public class User {
    protected String name;
    protected String id;
    protected String password;


    public User(String name, String id, String password) {
        this.id = id;
        this.name = name;
        this.password = password;

    }
    public String getId(){
        return id;
    }
    public void displayInfo(){
        System.out.println("Name:"+name);
        System.out.println("UserId:"+id);
    }

}

class Admin extends User {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_ID = "e20221526";

    public Admin() {
        super(ADMIN_USERNAME, ADMIN_USERNAME, ADMIN_ID);
    }

    public boolean verifyAdmin(String username, String id, String password) {
        return ADMIN_ID.equals(id) && ADMIN_USERNAME.equals(username)
                && ADMIN_PASSWORD.equals(password);
    }
    public void viewRegistration(Library library){
        System.out.println("=====> List of user registration <=====");
        library.displayRegistration();
    }
    public void viewBooks(Library library){
        System.out.println("=====> List of books in the library <=====");
        library.displayBooks();
    }
}
class Member extends User{
    public Member(String name, String id, String password) {
        super(name, id, password);
    }


    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}



