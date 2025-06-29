package uz.pdp.renderer;

import uz.pdp.model.Cart;
import uz.pdp.model.Cart.Item;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.util.List;

public final class CartItemRenderer {
    public static String render(Cart cart, ProductService productService) {
        StringBuilder sb = new StringBuilder();

        List<Item> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            return "Cart is empty.";
        }

        for (Item item : items) {
            Product product = productService.get(item.getProductId());
            if (product != null) {
                sb.append(product.getName()).append(" - ")
                        .append("$").append(product.getPrice()).append(" - ")
                        .append(item.getQuantity()).append(" pcs. \n");
            } else {
                sb.append(String.format("Product not found for id: %s", item.getProductId()));
            }
        }

        return sb.toString();
    }
}