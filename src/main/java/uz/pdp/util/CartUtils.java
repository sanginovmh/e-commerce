package uz.pdp.util;

import lombok.RequiredArgsConstructor;
import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public final class CartUtils {
    public static double calculatePrice(
            Cart cart,
            ProductService productService
    ) throws InvalidCartException,
            IOException {
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
