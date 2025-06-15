package uz.pdp;

import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.Scanner;

public class Alpha {
    static UserService userService = new UserService();
    static CategoryService categoryService = new CategoryService();
    static ProductService productService = new ProductService();
    static CartService cartService = new CartService();

    static Scanner strScanner = new Scanner(System.in);
    static Scanner intScanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

    }
}
