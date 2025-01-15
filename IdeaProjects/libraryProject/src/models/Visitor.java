package models;

public class Visitor extends User{
    public Visitor(String name, String email, String password,String imgPath) {
        super(name, email, password,imgPath);
        this.role = "visitor";
    }
    //    @Override
//    public String toString() {
//        return "Visitor{" +
//                "name='" + name + '\'' +
//                ", password='" + password + '\'' +
//                ", email='" + email + '\'' +
//                "} " + super.toString();
//    }
}
