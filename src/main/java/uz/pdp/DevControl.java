package uz.pdp;

import uz.pdp.service.*;
import uz.pdp.model.User;

public class DevControl {
    public static void main(String[] args) throws Exception {
        UserService userService = new UserService();
        userService.add(new User("Admin", "admin", "admin", User.UserRole.ADMIN));
    }
}