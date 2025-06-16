package uz.pdp.util;

import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.io.IOException;
import java.util.List;

public class CartUtils {
    /**
     * Converts the cart items to a pretty string format for display.
     *
     * @param cart           The cart containing items.
     * @return A formatted string representing the cart items.
     */
    public static String toPrettyStringItems(Cart cart) {
        ProductService productService = new ProductService();
        StringBuilder sb = new StringBuilder();
        List<Cart.Item> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            return "Cart is empty.";
        }
        for (Cart.Item item : items) {
            Product product = productService.get(item.getProductId());
            sb.append(product.getName()).append(", ")
                    .append("$").append(product.getPrice()).append(", ")
                    .append(item.getQuantity()).append("pcs. \n");
        }
        return sb.toString();
    }


    public static double calculatePrice(Cart cart) throws InvalidCartException, IOException {
        ProductService productService = new ProductService();
        double totalPrice = 0.0;
        List<Cart.Item> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new InvalidCartException("Cart is empty.");
        }
        for (Cart.Item item : items) {
            Product product = productService.get(item.getProductId());
            if (product == null || !product.isActive()) {
                throw new InvalidCartException("Product with ID " + item.getProductId() + " is not available.");
            }
            totalPrice += product.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }
}
