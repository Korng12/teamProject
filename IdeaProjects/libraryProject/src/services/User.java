package services;

abstract public class User {
    protected String name;
    protected String id;
    protected String password;
    protected String email;

    public User(String email, String password) {

        this.email = email;
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


