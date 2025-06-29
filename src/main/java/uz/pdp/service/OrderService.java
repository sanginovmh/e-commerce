package uz.pdp.service;

import uz.pdp.abstraction.OrderBuilder;
import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidOrderException;
import uz.pdp.model.Cart;
import uz.pdp.model.Order;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.record.UserInfo;
import uz.pdp.util.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


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
        return orders.stream()
                .filter(o -> o.isActive() && o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Order> getAll() {
        return orders.stream()
                .filter(Order::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean update(UUID id, Order order) {
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
            Cart cart, User user,
            Function<UUID, User> getIgnoreActiveSellerById,
            Function<UUID, Product> getProductById
    ) throws IOException, InvalidOrderException {
        OrderBuilder orderBuilder = new OrderBuilder(cart);
        return orderBuilder.buildNewOrder(
                new UserInfo(user.getId(), user.getUsername(), user.getFullName()),
                getIgnoreActiveSellerById,
                getProductById);
    }

    public List<Order> getByCustomerId(UUID id) {
        Predicate<Order> matchesId = o -> o.getCustomer().getId().equals(id);

        return orders.stream()
                .filter(Order::isActive)
                .filter(matchesId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Order> filterTotalHigherThan(double amount) {
        Predicate<Order> totalHigherThanAmount = o -> o.getGrandTotal() > amount;

        return orders.stream()
                .filter(Order::isActive)
                .filter(totalHigherThanAmount)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Order> filterTotalLowerThan(double amount) {
        Predicate<Order> totalLowerThanAmount = o -> o.getGrandTotal() < amount;

        return orders.stream()
                .filter(Order::isActive)
                .filter(totalLowerThanAmount)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, orders);
    }

    private List<Order> loadFromFile() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Order.class);
    }
}
