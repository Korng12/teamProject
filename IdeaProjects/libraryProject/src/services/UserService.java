package services;
import models.User;
import models.Visitor;
import utils.CSVUtils;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private static final String REGISTER_INFO ="D:\\OneDrive - itc.edu.kh\\teamProject\\IdeaProjects\\libraryProject\\src\\data\\registerInfo.csv";
    private static final int NAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 2;
    private static final int EMAIL_INDEX = 1;
    private Map<String,User> userMap; // HashMap for fast lookup(we plan to use email as a key )
    private List<User> userList;  // ArrayList store user in order
    public UserService(){
        this.userMap=new HashMap<>();
        this.userList=new ArrayList<>();
        loadUsers();
    }
//    public List<User> loadUsers(){
//        List<User> visitors=new ArrayList<User>();
//        Map<String,User> userMap =new HashMap<>();
//        List<String[]> rows = CSVUtils.readCSV(REGISTER_INFO);
//        for(String[] row: rows){
//            User visitor=new Visitor(row[0],row[1],row[2]);
//            visitors.add(visitor);
//        }
//        return visitors;
//    }
public void loadUsers() {
    List<String[]> rows = CSVUtils.readCSV(REGISTER_INFO);
    for (String[] row : rows) {
        if(row.length>=3){
            User visitor = new Visitor(row[NAME_INDEX], row[EMAIL_INDEX],row[PASSWORD_INDEX]);
            userMap.put(visitor.getEmail(), visitor);  // Add to HashMap for fast lookup
            userList.add(visitor);  // Add to ArrayList for displaying and admin operations
            System.out.println("Loading Registration information successfully");
        }else{
            System.err.println("Invalid row in csv: "+String.join(",",row));
        }

    }
}
    public boolean validateUser(String email,String password){
        User user =userMap.get(email);
        if(user !=null && user.getPassword().equals(password)){
            return true;
        }
        return false;
    }
    public boolean registerUser(User newUser){
        if (userMap.containsKey(newUser.getEmail())){
            return false;
        }
        userMap.put(newUser.getEmail(),newUser);
        userList.add(newUser);
        CSVUtils.writeCSV(REGISTER_INFO, (List<String>) newUser);
        return true;
    }
    // Get user by email
    public User getUserByEmail(String email) {
        return userMap.get(email);
    }

    private List<String[]> convertUsersToStringArray(List<User> users) {
        List<String[]> data = new ArrayList<>();
        for (User user : users) {
            data.add(new String[] {user.getName(), user.getPassword(), user.getEmail()});
        }
        return data;
    }

    public static void main(String[] args) {
       new UserService();


     }


}
