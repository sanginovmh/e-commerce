package uz.pdp.util;

import lombok.RequiredArgsConstructor;
import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart.Item;
import uz.pdp.model.Product;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public final class CartUtils {
    public static double calculatePrice(List<Item> items, Function<UUID, Product> getProductById)
            throws InvalidCartException, IOException {
        if (items.isEmpty()) {
            throw new InvalidCartException("Cannot calculate empty cart.");
        }

        double totalPrice = 0.0;

        for (Item item : items) {
            Product product = getProductById.apply(item.getProductId());
            if (product == null) {
                throw new InvalidCartException("Product is not available.");
            }

            totalPrice += product.getPrice() * item.getQuantity();
        }

        return totalPrice;
    }
}
