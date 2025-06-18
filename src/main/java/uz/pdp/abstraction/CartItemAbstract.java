package uz.pdp.abstraction;

import lombok.RequiredArgsConstructor;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;
import uz.pdp.model.Cart.Item;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public final class CartItemAbstract {
    private final Cart cart;

    public void addItemToCart(Product product, int quantity) throws IllegalArgumentException {
        Item existing = getItemInCart(product.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            return;
        }

        if (quantity > 0 && product.getQuantity() >= quantity) {
            Item item = new Item(product.getId(), quantity);
            cart.getItems().add(item);
        } else {
            throw new IllegalArgumentException("Invalid quantity for product: " + product.getName());
        }
    }

    public void buyItemsInCart(ProductService productService) throws IOException {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty, cannot proceed with purchase.");
        }
        for (Item item : cart.getItems()) {
            productService.purchaseProducts(item.getProductId(), item.getQuantity());
        }
        cart.setPaid(true);
    }

    public void removeItemFromCart(Product product) throws IllegalArgumentException {
        cart.getItems().removeIf(item -> item.getProductId().equals(product.getId()));
    }

    private Item getItemInCart(UUID productId) {
        for (Item item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
}
