package uz.pdp.renderer;

import lombok.RequiredArgsConstructor;
import uz.pdp.model.User;

import java.util.List;

public final class UserRenderer {
    public static String render(List<User> list) {
        StringBuilder sb = new StringBuilder();
        for (User user : list) {
            sb.append(String.format("%-20s %-15s %-7s\n",
                    user.getFullName(),
                    user.getUsername(),
                    user.getRole()));
        }
        return sb.toString();
    }
}
