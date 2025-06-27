package uz.pdp;

import uz.pdp.exception.InvalidCartException;
import uz.pdp.model.Cart;
import uz.pdp.model.Order;
import uz.pdp.model.Product;
import uz.pdp.renderer.OrderRenderer;
import uz.pdp.service.*;
import uz.pdp.model.User;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class DevControl {
    public static void main(String[] args) throws Exception {
        ProductService productService = new ProductService();
        CartService cartService = new CartService();
        UserService userService = new UserService();

        Cart cart = cartService.get(UUID.fromString("d583c327-5068-40ee-853d-becd7ebce948"));

        OrderService orderService = new OrderService();
        Order order =
                orderService.buildNewOrder(cart, userService.findByUsername("madi"), userService::getIgnoreActive, productService::get);

        System.out.println(OrderRenderer.render(order));
    }
}