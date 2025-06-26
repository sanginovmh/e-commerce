package uz.pdp.abstraction;

import lombok.RequiredArgsConstructor;
import uz.pdp.exception.InvalidOrderException;
import uz.pdp.model.Cart;
import uz.pdp.model.Order;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;
import uz.pdp.util.CartUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class OrderBuilder {
    private final Cart cart;

    public Order buildNewOrder(ProductService productService, User user, UserService userService) throws IOException, InvalidOrderException {
        Order order = new Order();

        order.setCartId(cart.getId());
        buildOrderCustomer(order, user);
        buildOrderBoughtItems(order, productService, userService);
        order.setGrandTotal(CartUtils.calculatePrice(cart, productService));

        return order;
    }

    private void buildOrderCustomer(Order order, User user) {
        Order.Customer customer = new Order.Customer();

        customer.setId(user.getId());
        customer.setFullName(user.getFullName());
        customer.setUsername(user.getUsername());

        order.setCustomer(customer);
    }

    private void buildOrderBoughtItems(Order order, ProductService productService, UserService userService)
        throws InvalidOrderException {
        List<Cart.Item> items = cart.getItems();
        if (items.isEmpty()) {
            throw new InvalidOrderException("Cannot create order because cart has no items.");
        }

        List<Order.BoughtItem> boughtItems = new ArrayList<>();
        for (Cart.Item item : items) {
            Order.BoughtItem boughtItem = new Order.BoughtItem();

            Product product = productService.get(item.getProductId());
            User seller = userService.getIgnoreActive(product.getSellerId());

            buildOrderBoughtItemSeller(boughtItem, seller);
            buildOrderBoughtItem(boughtItem, product, item);

            boughtItems.add(boughtItem);
        }

        order.setBoughtItems(boughtItems);
    }

    private void buildOrderBoughtItemSeller(
            Order.BoughtItem boughtItem,
            User seller) {
        Order.Seller orderSeller = new Order.Seller();
        orderSeller.setId(seller.getId());
        orderSeller.setFullName(seller.getFullName());
        orderSeller.setUsername(seller.getUsername());
        orderSeller.setActive(seller.isActive());

        boughtItem.setSeller(orderSeller);
    }

    private void buildOrderBoughtItem(Order.BoughtItem boughtItem, Product product, Cart.Item item) {
        boughtItem.setProductId(product.getId());
        boughtItem.setProduct(product.getName());
        boughtItem.setAmountBought(item.getQuantity());
        boughtItem.setPricePerPsc(product.getPrice());
        boughtItem.setTotalPaid(product.getPrice() * item.getQuantity());
    }
}
