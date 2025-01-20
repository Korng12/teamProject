package models;

public class Visitor extends User{
    public Visitor(String name, String email, String password,String imgPath) {
        super(name, email, password,imgPath);
        this.role = "visitor";
    }

}
