package uz.pdp;

import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

public class Testing {
    public static void main(String[] args) throws Exception {
        UserService userService = new UserService();
        CategoryService categoryService = new CategoryService();
        ProductService productService = new ProductService();
        CartService cartService = new CartService();

        User admin = userService.getByUsername("admin");
        System.out.println("Admin id: " + admin.getId());
        Category category = new Category("Electronics", null);
        categoryService.add(category);
        Product product = new Product("Smartphone", 1000.0, 10, category.getId(), admin.getId());
        productService.add(product);
        User customer = new User("John", "john", "pass", User.UserRole.CUSTOMER);
        userService.add(customer);
        Cart cart = new Cart(customer.getId());
        cartService.add(cart);
        cartService.addItemToCart(customer.getId(), product, 2);
        cartService.buyCart(customer.getId(), productService);
    }
}
