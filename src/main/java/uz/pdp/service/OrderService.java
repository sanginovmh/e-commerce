package uz.pdp.service;

import uz.pdp.abstraction.OrderBuilder;
import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidOrderException;
import uz.pdp.model.Cart;
import uz.pdp.model.Cart.Item;
import uz.pdp.model.Order;
import uz.pdp.model.Order.Customer;
import uz.pdp.model.Order.Seller;
import uz.pdp.model.Order.BoughtItem;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.util.CartUtils;
import uz.pdp.util.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OrderService implements BaseService<Order> {
    private static final String FILE_NAME = "orders.json";
    private List<Order> orders;

    public OrderService() {
        try {
            orders = loadFromFile();
        } catch (IOException e) {
            orders = new ArrayList<>();
        }
    }

    @Override
    public void add(Order order) throws IOException {
        orders.add(order);
        save();
    }

    @Override
    public Order get(UUID id) {
        for (Order order : orders) {
            if (order.isActive() && order.getId().equals(id)) {
                return order;
            }
        }
        return null;
    }

    @Override
    public List<Order> getAll() {
        List<Order> actives = new ArrayList<>();
        for (Order order : orders) {
            if (order.isActive()) {
                actives.add(order);
            }
        }
        return actives;
    }

    @Override
    public boolean update(UUID id, Order order) throws IOException {
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Order existing = get(id);
        if (existing != null) {
            existing.setActive(false);
            existing.touch();

            save();
        }
    }

    @Override
    public void clearAndSave() throws IOException {
        orders.clear();
        save();
    }

    public Order buildNewOrder(
            Cart cart,
            ProductService productService,
            User user,
            UserService userService
    ) throws IOException, InvalidOrderException {
        OrderBuilder orderBuilder = new OrderBuilder(cart);
        return orderBuilder.buildNewOrder(productService, user, userService);
    }

    public List<Order> getByCustomerId(UUID id) {
        List<Order> ordersByCustomers = new ArrayList<>();
        for (Order order : orders) {
            if (order.isActive()
                    && order.getCustomer().getId().equals(id)) {
                ordersByCustomers.add(order);
            }
        }

        return ordersByCustomers;
    }

    public List<Order> filterHigherThan(double amount) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.isActive()
                    && order.getGrandTotal() > amount) {
                filtered.add(order);
            }
        }

        return filtered;
    }

    public List<Order> filterLowerThan(double amount) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.isActive()
                    && order.getGrandTotal() < amount) {
                filtered.add(order);
            }
        }

        return filtered;
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, orders);
    }

    private List<Order> loadFromFile() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Order.class);
    }
}
