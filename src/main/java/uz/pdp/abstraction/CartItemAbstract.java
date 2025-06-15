package uz.pdp.abstraction;

import lombok.RequiredArgsConstructor;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.io.IOException;

@RequiredArgsConstructor
public class CartItemAbstract {
    private final Cart cart;

    /**
     * Adds an item to the cart if the quantity is valid and the product is not already in the cart.
     *
     * @param product  The product to add to the cart.
     * @param quantity The quantity of the product to add.
     * @throws IllegalArgumentException if the quantity is invalid or the product is already in the cart.
     */
    public void addItemToCart(Product product, int quantity) throws IllegalArgumentException {
        if (hasProductInCart(product)) {
            updateItemInCart(product, quantity);
            return;
        }
        if (isQuantityValid(product, quantity)) {
            Cart.Item item = new Cart.Item(product.getId(), quantity);
            cart.getItems().add(item);
        } else {
            throw new IllegalArgumentException("Invalid quantity for product: " + product.getName());
        }
    }

    /**
     * Updates the quantity of an item in the cart.
     *
     * @param product  The product to update.
     * @param quantity The new quantity for the product.
     * @throws IllegalArgumentException if the quantity is invalid or the product is not in the cart.
     */
    public void updateItemInCart(Product product, int quantity) throws IllegalArgumentException {
        if (isQuantityValid(product, quantity)) {
            for (Cart.Item item : cart.getItems()) {
                if (item.getProductId().equals(product.getId())) {
                    item.setQuantity(quantity);
                    return;
                }
            }
            throw new IllegalArgumentException("Product not found in cart: " + product.getName());
        } else {
            throw new IllegalArgumentException("Invalid quantity for product: " + product.getName());
        }
    }

    /**
     * Buys all items in the cart using the provided ProductService.
     *
     * @param productService The service to handle product purchases.
     * @throws IllegalArgumentException if the cart is empty or has no items.
     */
    public void buyItemsInCart(ProductService productService) throws IOException {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty, cannot proceed with purchase.");
        }
        for (Cart.Item item : cart.getItems()) {
            productService.buyProduct(item.getProductId(), item.getQuantity());
        }
        cart.setPaid(true);
    }

    /**
     * Evaluates the total price of items in the cart using the provided ProductService.
     *
     * @param productService The service to retrieve product details.
     * @return The total price of all items in the cart.
     */
    public double evaluatePrice(ProductService productService) {
        double totalPrice = 0.0;
        for (Cart.Item item : cart.getItems()) {
            Product product = productService.get(item.getProductId());
            if (product != null) {
                totalPrice += product.getPrice() * item.getQuantity();
            }
        }
        return totalPrice;
    }

    /**
     * Removes an item from the cart.
     *
     * @param product The product to remove from the cart.
     * @throws IllegalArgumentException if the product is not in the cart.
     */
    public void removeItemFromCart(Product product) throws IllegalArgumentException {
        cart.getItems().removeIf(item -> item.getProductId().equals(product.getId()));
    }

    /**
     * Checks if the cart contains a specific product.
     *
     * @param p The product to check.
     * @return true if the product is in the cart, false otherwise.
     */
    private boolean isQuantityValid(Product p, int q) {
        return q > 0 && p.getQuantity() > q;
    }

    /**
     * Checks if the cart already contains a product.
     *
     * @param p The product to check.
     * @return true if the product is already in the cart, false otherwise.
     */
    private boolean hasProductInCart(Product p) {
        return cart.getItems().stream().anyMatch(item -> item.getProductId().equals(p.getId()));
    }
}
