package uz.pdp;

import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.UUID;

public class Testing {
    public static void main(String[] args) throws Exception {
        ProductService productService = new ProductService();
        UserService userService = new UserService();

        System.out.println(productService.getByName("Laptop"));
        System.out.println(userService.getByUsername("admin"));
    }
}
