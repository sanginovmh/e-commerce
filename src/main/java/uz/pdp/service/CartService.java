package uz.pdp.service;

import uz.pdp.abstraction.CartItemAbstract;
import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.util.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CartService implements BaseService<Cart> {
    private static final String FILE_NAME = "carts.json";
    private List<Cart> carts;

    public CartService() {
        try {
            carts = readCarts();
        } catch (IOException e) {
            carts = new ArrayList<>();
        }
    }

    @Override
    public void add(Cart cart) throws IOException, InvalidCartException {
        carts = readCarts();
        if (!hasCart(cart.getCustomerId())) {
            carts.add(cart);
            save();
        } else {
            throw new InvalidCartException("Cart for this customer already exists.");
        }
    }

    @Override
    public Cart get(UUID id) {
        for (Cart cart : carts) {
            if (cart.isActive() && cart.getId().equals(id)) {
                return cart;
            }
        }
        return null;
    }

    @Override
    public boolean update(UUID id, Cart cart) throws IOException {
        Cart found = get(id);
        if (found != null) {
            found.setPaid(cart.isPaid());

            save();
            return true;
        }
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Cart found = get(id);
        if (found != null) {
            found.setActive(false);

            save();
        }
    }

    public void buyCart(UUID customerId, ProductService productService) throws InvalidCartException, IOException {
        Cart cart = getByCustomerId(customerId);
        if (isValidCart(cart)) {
            CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
            cartItemAbstract.buyItemsInCart(productService);
            remove(cart.getId());
            save();
        } else {
            throw new InvalidCartException("Cart not found or invalid for customer: " + customerId);
        }
    }

    private boolean isValidCart(Cart cart) {
        if (cart == null || cart.isPaid() || cart.getItems() == null || cart.getItems().isEmpty()) {
            return false;
        }
        for (Cart.Item item : cart.getItems()) {
            if (item.getQuantity() <= 0) {
                return false;
            }
        }
        return true;
    }

    public void evaluatePrice(UUID customerId, ProductService productService)
            throws InvalidCartException, IOException {
        Cart cart = getByCustomerId(customerId);
        if (cart == null) {
            throw new InvalidCartException("Cart not found for customer: " + customerId);
        }
        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        double totalPrice = cartItemAbstract.evaluatePrice(productService);
    }

    public Cart getByCustomerId(UUID customerId) {
        if (hasCart(customerId)) {
            for (Cart cart : carts) {
                if (cart.isActive() && cart.getCustomerId().equals(customerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    public boolean hasCart(UUID customerId) {
        for (Cart cart : carts) {
            if (cart.isActive() && cart.getCustomerId().equals(customerId)) {
                return true;
            }
        }
        return false;
    }

    public void addItemToCart(UUID customerId, Product product, int quantity) throws InvalidCartException, IllegalArgumentException, IOException {
        Cart cart = getByCustomerId(customerId);
        if (cart == null) {
            throw new InvalidCartException("Cart not found for customer: " + customerId);
        }

        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        cartItemAbstract.addItemToCart(product, quantity);
        save();
    }

    public void updateItemInCart(UUID customerId, Product product, int quantity) throws InvalidCartException, IllegalArgumentException, IOException {
        Cart cart = getByCustomerId(customerId);
        if (cart == null) {
            throw new InvalidCartException("Cart not found for customer: " + customerId);
        }

        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        cartItemAbstract.updateItemInCart(product, quantity);
        save();
    }

    public void removeItemFromCart(UUID customerId, Product product) throws InvalidCartException, IOException {
        Cart cart = getByCustomerId(customerId);
        if (cart == null) {
            throw new InvalidCartException("Cart not found for customer: " + customerId);
        }

        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        cartItemAbstract.removeItemFromCart(product);
        save();
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, carts);
    }

    private List<Cart> readCarts() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Cart.class);
    }

    public void clear() throws IOException {
        carts = new ArrayList<>();
        save();
    }
}
