package services;

public class Admin extends User {
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";


    public Admin() {
        super(ADMIN_EMAIL , ADMIN_PASSWORD);
    }
    public boolean verifyAdmin(String email, String password) {
        return ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password);
    }

}