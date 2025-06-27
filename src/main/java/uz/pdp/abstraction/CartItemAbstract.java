package uz.pdp.abstraction;

import lombok.RequiredArgsConstructor;
import uz.pdp.exception.InvalidCartException;
import uz.pdp.function.CheckedBiConsumer;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.model.Cart.Item;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public final class CartItemAbstract {
    private final Cart cart;

    public void addItemToCart(Product product, int quantity) throws IllegalArgumentException {
        UUID productId = product.getId();

        Item existing = getItemInCart(productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            return;
        }

        if (quantity > 0 && product.getQuantity() >= quantity) {
            Item item = new Item(productId, quantity);
            cart.getItems().add(item);
        } else {
            throw new IllegalArgumentException("Invalid quantity for product: " + product.getName());
        }
    }

    public void buyItemsInCart(CheckedBiConsumer<UUID, Integer> purchaseProductsByItemInfo)
            throws InvalidCartException, IOException {
        List<Item> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new InvalidCartException("Cart is empty, cannot proceed with purchase.");
        }

        for (Item item : items) {
            purchaseProductsByItemInfo.accept(item.getProductId(), item.getQuantity());
        }

        cart.setPaid(true);
    }

    public void removeItemFromCart(Product product) throws IllegalArgumentException {
        cart.getItems().removeIf(i -> i.getProductId().equals(product.getId()));
    }

    private Item getItemInCart(UUID productId) {
        return cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
