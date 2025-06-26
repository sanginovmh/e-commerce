package uz.pdp.util;

import lombok.RequiredArgsConstructor;
import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart;
import uz.pdp.model.Cart.Item;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public final class CartUtils {
    public static double calculatePrice(Cart cart, ProductService productService)
            throws InvalidCartException, IOException {
        double totalPrice = 0.0;

        List<Item> items = getItemsAndEnforceNonNullAndNotEmpty(cart);

        for (Item item : items) {
            totalPrice += getPriceOfProductOfItem(productService, item) * item.getQuantity();
        }

        return totalPrice;
    }

    private static List<Item> getItemsAndEnforceNonNullAndNotEmpty(Cart cart) throws InvalidCartException {
        List<Item> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new InvalidCartException("Cart is empty.");
        }

        return items;
    }

    private static double getPriceOfProductOfItem(ProductService productService, Item item) {
        Product product = productService.get(item.getProductId());
        if (product == null || !product.isActive()) {
            throw new InvalidCartException("Product with ID " + item.getProductId() + " is not available.");
        }

        return product.getPrice();
    }
}
