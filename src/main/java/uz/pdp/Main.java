package uz.pdp;

import uz.pdp.model.User;
import uz.pdp.service.UserService;

public class Main {
    public static void main(String[] args) throws Exception {
        UserService userService = new UserService();

        User user = new User("Ozodbek", "ozod777", "123", User.UserRole.ADMIN);

        userService.add(user);

        System.out.println(userService.get(user.getId()));
    }
}