package uz.pdp.service;

import uz.pdp.abstraction.CartItemAbstract;
import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidCartException;
import uz.pdp.exception.InvalidCartItemException;
import uz.pdp.model.Cart;
import uz.pdp.model.Product;
import uz.pdp.util.FileUtils;
import uz.pdp.model.Cart.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class CartService implements BaseService<Cart> {
    private static final String FILE_NAME = "carts.json";
    private List<Cart> carts;

    public CartService() {
        try {
            carts = loadFromFile();
        } catch (IOException e) {
            carts = new ArrayList<>();
        }
    }

    @Override
    public void add(Cart cart) throws IOException, InvalidCartException {
        if (findByCustomerId(cart.getCustomerId()) != null) {
            throw new InvalidCartException("Cart for this customer already exists.");
        }

        carts.add(cart);

        save();
    }

    @Override
    public Cart get(UUID id) {
        return carts.stream()
                .filter(Cart::isActive)
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cart> getAll() {
        return carts.stream()
                .filter(Cart::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean update(UUID id, Cart cart) throws IOException {
        Cart existing = get(id);
        if (existing == null) {
            return false;
        }

        existing.setPaid(cart.isPaid());
        existing.touch();

        save();

        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Cart existing = get(id);
        if (existing == null) {
            return;
        }

        existing.setActive(false);
        existing.touch();

        save();
    }

    @Override
    public void clearAndSave() throws IOException {
        carts.clear();
        save();
    }

    public Cart findByCustomerId(UUID id) {
        return carts.stream()
                .filter(Cart::isActive)
                .filter(c -> c.getCustomerId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private boolean isValidCartIfFalseThenSanitize(Cart cart, ProductService productService) throws IOException {
        if (cart == null || cart.isPaid() || cart.getItems() == null || cart.getItems().isEmpty()) {
            return false;
        }


        for (Item item : cart.getItems()) {
            Product product = productService.get(item.getProductId());
            if (item.getQuantity() > product.getQuantity()) {
                sanitize(cart, product);

                return false;
            }
        }

        return true;
    }

    private void sanitize(Cart cart, Product product) throws IOException {
        if (product == null || !product.isActive() || product.getQuantity() <= 0) {
            CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
            cartItemAbstract.removeItemFromCart(product);
            cart.touch();

            save();
        }
    }

    public void checkoutCart(Cart cart, ProductService productService) throws IOException, InvalidCartItemException {
        if (!isValidCartIfFalseThenSanitize(cart, productService)) {
            throw new InvalidCartItemException("Product is out of stock or quantity is invalid. Item removed from cart.");
        }

        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        cartItemAbstract.buyItemsInCart(productService);
        remove(cart.getId());

        save();
    }

    public void addItemToCart(UUID customerId, Product product, int quantity)
            throws InvalidCartException, IllegalArgumentException, IOException {
        Cart cart = findByCustomerId(customerId);
        if (cart == null) {
            throw new InvalidCartException("Cart not found for given customer ID.");
        }

        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        cartItemAbstract.addItemToCart(product, quantity);
        cart.touch();

        save();
    }

    public void removeItemFromCart(Cart cart, Product product)
            throws InvalidCartException, IOException {
        CartItemAbstract cartItemAbstract = new CartItemAbstract(cart);
        cartItemAbstract.removeItemFromCart(product);
        cart.touch();

        save();
    }

    public void removeByCustomerId(UUID id) throws IOException {
        Cart cart = findByCustomerId(id);
        if (cart == null) {
            throw new InvalidCartException("Cart not found for given customer ID.");
        }

        remove(cart.getId());
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, carts);
    }

    private List<Cart> loadFromFile() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Cart.class);
    }
}
