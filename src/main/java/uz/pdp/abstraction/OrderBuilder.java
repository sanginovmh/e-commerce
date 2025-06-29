package uz.pdp.abstraction;

import lombok.RequiredArgsConstructor;
import uz.pdp.exception.InvalidOrderException;
import uz.pdp.model.Cart;
import uz.pdp.model.Cart.Item;
import uz.pdp.model.Order;
import uz.pdp.model.Order.BoughtItem;
import uz.pdp.model.Order.Customer;
import uz.pdp.model.Order.Seller;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.record.UserInfo;
import uz.pdp.util.CartUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public final class OrderBuilder {
    private final Cart cart;

    public Order buildNewOrder(
            UserInfo userInfo,
            Function<UUID, User> getIgnoreActiveSellerById,
            Function<UUID, Product> getProductById
    ) throws IOException, InvalidOrderException {
        Order order = new Order();

        order.setCartId(cart.getId());
        order.setCustomer(buildOrderCustomer(userInfo));
        order.setBoughtItems(buildOrderBoughtItems(getProductById, getIgnoreActiveSellerById));
        order.setGrandTotal(CartUtils.calculatePrice(cart.getItems(), getProductById));

        return order;
    }

    private Customer buildOrderCustomer(UserInfo userInfo) {
        Customer customer = new Customer();

        customer.setId(userInfo.getId());
        customer.setFullName(userInfo.getFullName());
        customer.setUsername(userInfo.getUsername());

        return customer;
    }

    private List<BoughtItem> buildOrderBoughtItems(
            Function<UUID, Product> getProductById,
            Function<UUID, User> getIgnoreActiveSellerById
    ) throws InvalidOrderException {
        List<Item> items = cart.getItems();
        if (items.isEmpty()) {
            throw new InvalidOrderException("Cannot create order from an empty cart.");
        }

        List<BoughtItem> boughtItems = new ArrayList<>();

        items.forEach(i -> {
            Product product = getProductById.apply(i.getProductId());
            User userSeller = getIgnoreActiveSellerById.apply(product.getSellerId());

            BoughtItem boughtItem = buildBoughtItem(product, i.getQuantity());
            boughtItem.setSeller(buildBoughtItemSeller(userSeller));

            boughtItems.add(boughtItem);
        });

        return boughtItems;
    }

    private Seller buildBoughtItemSeller(User userSeller) {
        Seller orderSeller = new Seller();
        orderSeller.setId(userSeller.getId());
        orderSeller.setFullName(userSeller.getFullName());
        orderSeller.setUsername(userSeller.getUsername());
        orderSeller.setActive(userSeller.isActive());

        return orderSeller;
    }

    private BoughtItem buildBoughtItem(Product product, int itemQuantity) {
        BoughtItem boughtItem = new BoughtItem();

        boughtItem.setProductId(product.getId());
        boughtItem.setProduct(product.getName());
        boughtItem.setAmountBought(itemQuantity);
        boughtItem.setPricePerPsc(product.getPrice());
        boughtItem.setTotalPaid(product.getPrice() * itemQuantity);

        return boughtItem;
    }
}
