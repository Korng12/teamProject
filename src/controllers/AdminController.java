package controllers;

import models.Book;
import models.User;
import services.AdminService;
import java.util.List;

public class AdminController {
    private AdminService adminService;
    public AdminController() {
        this.adminService=new AdminService();
    }
    public int totalUsers(){
        return adminService.getTotalUsers();
    }
    public int totalBooks(){
        return adminService.getTotalUsers();
    }
    public int totalGenre(){
        return adminService.getTotalGenre();
    }
    public List<User> allUsers(){
        return adminService.getAllUsers();
    }
    public List<Book> allBooks(){
        return adminService.getALlBooks();
    }
    public int totalAvailableBooks(){
        return adminService.getTotalBooks();
    }
    public User fetchUserByEmail(String email){
        return adminService.getUserByEmail(email);
    }
    public boolean updateUser(String email,String newName,String newEmail,String newRole){
        return adminService.updateUser(email,newName,newEmail,newRole);
    }
    public boolean deleteUser(String email){
        return adminService.deleteUser(email);
    }
    public boolean addUser(User user){
        return adminService.addUser(user);
    }
}
