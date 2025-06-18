package uz.pdp.renderer;

import lombok.RequiredArgsConstructor;
import uz.pdp.model.Category;

import java.util.List;

@RequiredArgsConstructor
public final class CategoryRenderer {
    public static String render(List<Category> list) {
        StringBuilder sb = new StringBuilder();
        for (Category category : list) {
            sb.append(String.format("%-20s\n",
                    category.getName()));
        }

        return sb.toString();
    }
}
