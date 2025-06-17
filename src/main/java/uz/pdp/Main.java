package uz.pdp;

import uz.pdp.model.User;
import uz.pdp.service.UserService;

public class Main {
    public static void main(String[] args) throws Exception {
        User admin = new User("Admin Boss", "admin", "admin", User.UserRole.ADMIN);
        new UserService().add(admin);
    }
}