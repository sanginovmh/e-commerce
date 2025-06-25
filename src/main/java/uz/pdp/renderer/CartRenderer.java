package uz.pdp.renderer;

import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart;
import uz.pdp.model.User;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;
import uz.pdp.util.CartUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class CartRenderer {
    public static String adminRender(
            List<Cart> carts,
            UserService userService,
            ProductService productService) {
        StringBuilder sb = new StringBuilder();
        for (Cart cart : carts) {
            if (cart.isActive()) {
                UUID customerId = cart.getCustomerId();
                User customer = userService.get(customerId);

                if (customer != null) {
                    sb.append(String.format("%s:\n",
                            customer.getUsername()));
                    sb.append(render(cart, productService)).append("\n");
                } else {
                    sb.append(String.format("Error handling user id: %s", customerId));
                }
            }
        }

        return sb.toString();
    }

    public static String render(Cart cart, ProductService productService) {
        StringBuilder sb = new StringBuilder();

        sb.append(CartItemRenderer.render(cart, productService));
        try {
            sb.append(String.format("Total: $%.2f\n",
                    CartUtils.calculatePrice(cart, productService)));
        } catch (InvalidCartException | IOException e) {
            sb.append("Error calculating price: ").append(e.getMessage());
        }

        return sb.toString();
    }
}
