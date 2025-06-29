package uz.pdp;

import uz.pdp.model.User;
import uz.pdp.service.UserService;

import java.io.IOException;

public class DevControl {
    public static void main(String[] args) throws IOException {
        User user = new User();
        user.setRole(User.UserRole.ADMIN);
        user.setFullName("Boss");
        user.setUsername("admin");
        user.setPassword("admin");
        UserService userService = new UserService();
        userService.add(user);





    }
}