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

    }

    private static void clear() throws Exception {
        (new CartService()).clear();
        (new CategoryService()).clear();
        (new ProductService()).clear();
        (new UserService()).clear();
    }
}
