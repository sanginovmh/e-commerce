package uz.pdp.renderer;

import uz.pdp.model.Product;

import java.util.List;

public final class ProductRenderer {
    public static String render(List<Product> list) {
        StringBuilder sb = new StringBuilder();
        for (Product product : list) {
            sb.append(String.format("%-13s $%-8.2f qty: %-3d\n",
                    product.getName(), product.getPrice(), product.getQuantity()));
        }

        return sb.toString();
    }

    public static String render(Product product) {
        return String.format("%-13s $%-8.2f qty: %-3d\n",
                product.getName(), product.getPrice(), product.getQuantity());
    }
}
