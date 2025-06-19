package uz.pdp;

import uz.pdp.model.Cart;
import uz.pdp.model.Order;
import uz.pdp.model.Product;
import uz.pdp.service.CartService;
import uz.pdp.service.OrderService;
import uz.pdp.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws Exception {


        OrderService orderService = new OrderService();
        Order.Customer customer = new Order.Customer(UUID.randomUUID(), "aaaaaa", "user");
        Order.Seller seller = new Order.Seller(UUID.randomUUID(), " aaaa", "seller");
        Order order = new Order();
//        seller.setId(UUID.randomUUID());
//        seller.setFullName("aa aa");
//        seller.setUsername("seller");
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setCartId(UUID.randomUUID());
//        Product product = new Product("pamidor", 9.2, 9,UUID.randomUUID(),seller.getId());
//        List<Cart.Item> products = new ArrayList<>();
//        Cart.Item item = new Cart.Item();
//        item.setProductId(product.getId());
//        item.setQuantity(5);
//        Cart cart = new Cart();
//        CartService cartService = new CartService();
//        cartService.checkoutCart(cart, new ProductService());


    }
}