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

        Order.Customer customer = new Order.Customer(UUID.randomUUID(), "John Doe", "john_doe");
        List<Order.BoughtItem> boughtItems = new ArrayList<>();
        Order.Seller seller = new Order.Seller(UUID.randomUUID(), "Seller Seller", "seller_se");
        Order.BoughtItem item = new Order.BoughtItem(seller, UUID.randomUUID(), "Bananas", 40, 12.5, 40 * 12.5);
        Order.BoughtItem item2 = new Order.BoughtItem(seller, UUID.randomUUID(), "Cars", 2, 12000, 2 * 12000);
        boughtItems.add(item);
        boughtItems.add(item2);
        Order order = new Order(UUID.randomUUID(), customer, boughtItems, item.getTotalPaid() + item2.getTotalPaid());

        orderService.add(order);

        System.out.println(orderService.get(order.getId()));

    }
}