package uz.pdp;

import uz.pdp.exception.InvalidCategoryException;
import uz.pdp.exception.InvalidProductException;
import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.renderer.CartRenderer;
import uz.pdp.renderer.CategoryRenderer;
import uz.pdp.renderer.ProductRenderer;
import uz.pdp.renderer.UserRenderer;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Alpha {
    static UserService userService = new UserService();
    static CartService cartService = new CartService();
    static ProductService productService = new ProductService();
    static CategoryService categoryService = new CategoryService();

    static Scanner strScanner = new Scanner(System.in);
    static Scanner numScanner = new Scanner(System.in);

    static User currentUser = null;

    public static void main(String[] args) {
        while (true) {
            currentUser = null;
            initialPage();
        }
    }

    public static void initialPage() {
        System.out.print("""
                --- Welcome to Alpha! ---
                1. Login
                2. Register
                
                3. Exit
                
                input %\s""");
        switch (strScanner.nextLine()) {
            case "1" -> loginPage();
            case "2" -> registerPage();
            case "3" -> System.exit(0);
            default -> {
                System.out.println("Invalid input, try again!");
                waitClick();
                initialPage();
            }
        }
    }

    public static void loginPage() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = strScanner.nextLine();
        System.out.print("Password: ");
        String password = strScanner.nextLine();

        currentUser = userService.login(username, password);
        if (currentUser != null) {
            System.out.println("Login successful!");
            waitClick();
        } else {
            System.out.println("Invalid credentials, try again!");
            waitClick();
            initialPage();
            return;
        }
        while (true) {
            dashboardPage();
        }
    }

    public static void registerPage() {
        System.out.println("\n--- Register ---");
        System.out.print("Full Name: ");
        String fullName = strScanner.nextLine();
        System.out.print("Username: ");
        String username = strScanner.nextLine();
        System.out.print("Password: ");
        String password = strScanner.nextLine();
        System.out.print("Customer or Seller? (C/S): ");
        String roleInput = strScanner.nextLine().trim().toUpperCase();
        User.UserRole role;
        switch (roleInput) {
            case "C" -> role = User.UserRole.CUSTOMER;
            case "S" -> role = User.UserRole.SELLER;
            default -> {
                System.out.println("Invalid role, try again!");
                waitClick();
                registerPage();
                return;
            }
        }

        User newUser = new User(fullName, username, password, role);
        try {
            userService.add(newUser);
            currentUser = newUser;
            System.out.println("Registration successful!");
            waitClick();
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            waitClick();
            registerPage();
            return;
        }
        while (true) {
            dashboardPage();
        }
    }

    public static void dashboardPage() {
        if (currentUser.getRole().equals(User.UserRole.ADMIN)) {
            System.out.print("""
                    \n--- Admin Dashboard ---
                    1. Manage Users
                    2. Manage Categories
                    3. Manage Products
                    4. Manage Carts
                    
                    5. Browse Categories
                    6. Create New Admin
                    
                    7. Search Super
                    8. Search Global
                    
                    9. Logout
                    10. Exit
                    
                    input %\s""");
            switch (strScanner.nextLine()) {
                case "1" -> manageUsersPage();
                case "2" -> manageCategoriesPage();
                case "3" -> manageProductsPage();
                case "4" -> manageCartsPage();
                case "5" -> browseCategories();
                case "6" -> createNewAdminPage();
                case "7" -> searchSuperPage();
                case "8" -> searchGlobalPage();
                case "9" -> logout();
                case "10" -> System.exit(0);
                default -> {
                    System.out.println("Invalid input, try again!");
                    waitClick();
                    dashboardPage();
                }
            }
        } else if (currentUser.getRole().equals(User.UserRole.SELLER)) {
            System.out.print("""
                    \n--- Seller Dashboard ---
                    1. Browse Products
                    2. View Your Products
                    3. Remove Product
                    4. Add New Product
                    5. Search Global
                    6. Change Your Product Name
                    
                    7. Logout
                    
                    input %\s""");
            switch (strScanner.nextLine()) {
                case "1" -> browseCategories();
                case "2" -> viewYourProductsPage();
                case "3" -> removeSellerProductPage();
                case "4" -> addNewProductPage();
                case "5" -> searchGlobalPage();
                case "6" -> changeYourNameProductPage();
                case "7" -> logout();
            }
        } else if (currentUser.getRole().equals(User.UserRole.CUSTOMER)) {
            System.out.print("""
                    \n--- Customer Dashboard ---
                    1. Browse
                    2. View Cart
                    3. Search Global
                    
                    4. Logout
                    
                    input %\s""");
            switch (strScanner.nextLine()) {
                case "1" -> browseCategories();
                case "2" -> viewCustomerCartPage();
                case "3" -> searchGlobalPage();
                case "4" -> logout();
            }
        } else {
            System.out.println("Unknown role, logging out!");
            waitClick();
            logout();
        }
    }


    public static void manageUsersPage() {
        System.out.println("\n--- Manage Users ---");
        List<User> users = userService.getAll();
        System.out.println(UserRenderer.render(users));

        System.out.print("Remove user: ");
        String username = strScanner.nextLine();
        User user = userService.findByUsername(username);
        if (user != null) {
            if (user.getRole().equals(User.UserRole.ADMIN)) {
                System.out.println("Cannot remove an admin user!");
                waitClick();
                dashboardPage();
                return;
            }
            try {
                userService.remove(user.getId());
                System.out.println("User removed successfully!");
                waitClick();
            } catch (Exception e) {
                System.out.println("Failed to remove user: " + e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("User not found!");
            waitClick();
        }
    }

    public static void manageCategoriesPage() {
        System.out.print("""
                \n--- Manage Categories ---
                1. View All Categories
                2. Browse Categories
                3. Add New Category
                4. Remove Category
                5. Change name category
                input %\s""");
        switch (strScanner.nextLine()) {
            case "1" -> displayAllCategoriesPage();
            case "2" -> browseCategories();
            case "3" -> addNewCategoryPage();
            case "4" -> removeCategoryPage();
            case "5" -> changeNameCategoryPage();
            default -> {
                System.out.println("Invalid input, try again!");
                waitClick();
                manageCategoriesPage();
            }
        }
    }

    public static void changeNameCategoryPage() {
        displayAllCategoriesPage();
        System.out.print("Enter category name: ");
        Category category = categoryService.findByName(strScanner.nextLine());
        if (category != null) {
            System.out.print("Enter category new name: ");
            String categoryNewName = strScanner.nextLine();
            try {
                categoryService.updateCategoryName(category, categoryNewName);
                System.out.println("Category name changed successfully.");
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                waitClick();
            }catch (InvalidCategoryException e){
                System.out.println("Category exception: " + e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("Category not found.");
            waitClick();
        }
    }

    public static void displayAllCategoriesPage() {
        System.out.println("\n--- All Categories ---");
        List<Category> categories = categoryService.getAll();
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            waitClick();
        } else {
            System.out.println(CategoryRenderer.render(categories));
        }
        waitClick();
    }

    public static void changeYourNameProductPage() {
        viewYourProductsPage();
        System.out.print("Enter product name: ");
        Product product = productService.findByName(strScanner.nextLine());
        if (product != null) {
            System.out.print("Enter product new name: ");
            String productNewName = strScanner.nextLine();
            try {
                productService.updateProductName(product, productNewName);
                System.out.println("Product name changed successfully.");
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                waitClick();
            }catch (InvalidProductException e){
                System.out.println("Product exception: " + e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("Product not found.");
            waitClick();
        }

    }

    public static void browseCategories() {
        System.out.println("\n--- Browse ---");
        String up = "Root";
        List<Category> level;
        List<Product> products;
        while (true) {
            UUID categoryId = CategoryService.ROOT_UUID;
            Category upCategory = categoryService.findByName(up);
            if (!up.equals("Root")) {
                categoryId = upCategory.getId();
            }
            level = categoryService.getDecendents(categoryId);
            System.out.println("\n- " + upCategory.getName() + " -");
            if (!level.isEmpty()) {
                System.out.println(CategoryRenderer.render(level));
            } else {
                System.out.println("[no categories found]");
                waitClick();
            }
            System.out.print("Go to ('.' to go back): ");
            String goTo = strScanner.nextLine().trim();

            if (!goTo.equals(".")) {
                Category category = categoryService.findByName(goTo);
                if (category != null) {
                    level = categoryService.getDecendents(category.getId());
                    if (level.isEmpty()) {
                        products = productService.getByCategoryId(category.getId());
                        System.out.println("- " + category.getName() + " -");
                        if (!products.isEmpty()) {
                            System.out.println(ProductRenderer.render(products));
                            if (currentUser.getRole().equals(User.UserRole.CUSTOMER)) {
                                selectProductPage();
                            } else {
                                waitClick();
                            }
                        } else {
                            System.out.println("[no products found]");
                            waitClick();
                        }
                    } else {
                        up = goTo;
                    }
                } else {
                    System.out.println("Category not found.");
                    waitClick();
                }
            } else {
                if (!categoryId.equals(CategoryService.ROOT_UUID)) {
                    Category category = categoryService.get(categoryId);
                    Category parent = categoryService.get(category.getParentId());
                    if (parent != null) {
                        up = parent.getName();
                    } else {
                        up = "Root";
                    }
                } else {
                    return;
                }
            }
        }
    }

    public static void selectProductPage() {
        System.out.print("Select: ");
        String input = strScanner.nextLine().trim();
        if (!input.equals(".")) {
            Product product = productService.findByName(input);
            customerProductMenu(product);
        }
    }

    public static void customerProductMenu(Product product) {
        System.out.println("\n--- Product Menu ---");
        if (product == null) {
            System.out.println("Product not found!");
            waitClick();
            return;
        }

        System.out.println(ProductRenderer.render(product));
        System.out.print("""
                1. Add to Cart
                
                2. Back to Categories
                3. Back to Dashboard
                
                input %\s""");
        switch (strScanner.nextLine()) {
            case "1" -> addToCartPage(product);
            case "3" -> dashboardPage();
        }
    }

    public static void addToCartPage(Product product) {
        System.out.println("\n--- Add to Cart ---");
        System.out.print("Enter quantity: ");
        int quantity = numScanner.nextInt();
        UUID userId = currentUser.getId();

        Cart cart = cartService.findByCustomerId(userId);
        if (cart == null) {
            cart = new Cart(userId);
            try {
                cartService.add(cart);
                System.out.print("New cart created!");
                waitClick();
            } catch (Exception e) {
                System.out.println("Unable to create cart: " + e.getMessage());
                waitClick();
            }
        }
        try {
            cartService.addItemToCart(userId, product, quantity);
            System.out.println("Item added to cart!");
            waitClick();
        } catch (Exception e) {
            System.out.println("Unable to add product to cart: " + e.getMessage());
            waitClick();
        }
    }

    public static void addNewCategoryPage() {
        System.out.println("\n--- Add New Category ---");
        System.out.print("Category Name: ");
        String name = strScanner.nextLine();

        printLastCategories();

        if (name.trim().equalsIgnoreCase("Root")) {
            System.out.println("Category name cannot be 'root'.");
            waitClick();
            return;
        }
        System.out.print("Parent Category (or 'Root'): ");
        String parentName = strScanner.nextLine().trim();
        UUID parentId = CategoryService.ROOT_UUID;
        if (!parentName.equalsIgnoreCase("Root")) {
            Category parentCategory = categoryService.findByName(parentName);
            if (parentCategory != null) {
                if (!productService.isCategoryEmpty(parentId)) {
                    System.out.println("Parent category must be empty of products.");
                    waitClick();
                    return;
                }
                parentId = parentCategory.getId();
            } else {
                System.out.println("Parent category not found.");
                waitClick();
                return;
            }
        }
        Category newCategory = new Category(name, parentId);
        try {
            categoryService.add(newCategory);
            System.out.println("Category added successfully!");
            waitClick();
        } catch (Exception e) {
            System.out.println("Failed to add category: " + e.getMessage());
            waitClick();
        }
    }

    public static void removeCategoryPage() {
        System.out.println("\n--- Remove Category ---");
        System.out.print("Category Name: ");
        String name = strScanner.nextLine();
        Category category = categoryService.findByName(name);
        if (category != null) {
            try {
                categoryService.remove(category.getId());
                System.out.println("Category removed successfully!");
                waitClick();
            } catch (Exception e) {
                System.out.println("Failed to remove category: " +
                        e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("Category not found!");
            waitClick();
        }
    }

    public static void manageProductsPage() {
        System.out.print("""
                \n--- Manage Products ---
                1. View All Products
                2. Remove Product
                input %\s""");
        switch (strScanner.nextLine()) {
            case "1" -> displayAllProducts();
            case "2" -> removeProductPage();
            default -> {
                System.out.println("Invalid input, try again!");
                waitClick();
            }
        }
    }

    public static void displayAllProducts() {
        System.out.println("\n--- All Products ---");
        List<Product> products = productService.getAll();
        if (products.isEmpty()) {
            System.out.println("No products available.");
            waitClick();
        } else {
            System.out.println(ProductRenderer.render(products));
        }
        waitClick();
    }

    public static void removeProductPage() {
        System.out.println("\n--- Remove Product ---");
        System.out.print("Product Name: ");
        String name = strScanner.nextLine();
        Product product = productService.findByName(name);
        if (product != null) {
            try {
                productService.remove(product.getId());
                System.out.println("Product removed successfully!");
                waitClick();
            } catch (Exception e) {
                System.out.println("Failed to remove product: " + e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("Product not found!");
            waitClick();
        }
    }

    public static void manageCartsPage() {
        System.out.print("""
                \n--- Manage Carts ---
                1. View All Carts
                2. Remove Cart
                input %\s""");
        switch (strScanner.nextLine()) {
            case "1" -> viewAllCarts();
            case "2" -> removeCartPage();
            default -> {
                System.out.println("Invalid input, try again!");
                waitClick();
                manageCartsPage();
            }
        }
    }

    public static void viewAllCarts() {
        System.out.println("\n--- All Carts ---");
        List<Cart> carts = cartService.getAll();
        if (carts.isEmpty()) {
            System.out.println("No carts available.");
            waitClick();
        } else {
            System.out.println(CartRenderer.adminRender(carts, userService, productService));
        }
        waitClick();
    }

    public static void removeCartPage() {
        System.out.println("\n--- Remove Cart ---");
        System.out.print("Customer Username: ");
        String username = strScanner.nextLine();
        User user = userService.findByUsername(username);
        if (user != null) {
            try {
                cartService.removeByCustomerId(user.getId());
                System.out.println("Cart removed successfully!");
                waitClick();
            } catch (Exception e) {
                System.out.println("Failed to remove cart: " + e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("User not found!");
            waitClick();
        }
    }

    public static void createNewAdminPage() {
        System.out.println("\n--- Create New Admin ---");
        System.out.print("Full Name: ");
        String fullName = strScanner.nextLine();
        System.out.print("Username: ");
        String username = strScanner.nextLine();
        System.out.print("Password: ");
        String password = strScanner.nextLine();

        User newAdmin = new User(fullName, username, password, User.UserRole.ADMIN);
        try {
            userService.add(newAdmin);
            System.out.println("New admin created successfully!");
            waitClick();
        } catch (Exception e) {
            System.out.println("Failed to create admin: " + e.getMessage());
            waitClick();
        }
    }

    public static void searchSuperPage() {
        System.out.println("\n--- Search Super ---");
        System.out.print("Enter search term: ");
        // String searchTerm = strScanner.nextLine();
        // TODO implement search functionality
    }

    public static void searchGlobalPage() {
        System.out.println("\n--- Search Global ---");
        System.out.print("Enter search term: fd");
        // String searchTerm = strScanner.nextLine();
        // TODO implement global search functionality
    }

    public static void viewYourProductsPage() {
        System.out.println("\n--- Your Products ---");
        List<Product> products = productService.getBySeller(currentUser.getId());
        if (products.isEmpty()) {
            System.out.println("You have no products listed.");
            waitClick();
        } else {
            System.out.println(ProductRenderer.render(products));
        }
        waitClick();
    }

    public static void removeSellerProductPage() {
        System.out.println("\n--- Remove Your Product ---");
        System.out.print("Product Name: ");
        String name = strScanner.nextLine();
        Product product = productService.findByName(name);
        if (product != null && product.getSellerId().equals(currentUser.getId())) {
            try {
                productService.remove(product.getId());
                System.out.println("Product removed successfully!");
                waitClick();
            } catch (Exception e) {
                System.out.println("Failed to remove product: " + e.getMessage());
                waitClick();
            }
        } else {
            System.out.println("Product not found or you are not the seller!");
            waitClick();
        }
    }

    public static void addNewProductPage() {
        System.out.println("\n--- Add New Product ---");
        System.out.print("Product Name: ");
        String name = strScanner.nextLine();
        System.out.print("Enter Product Price: ");
        double price;
        try {
            price = numScanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Price must be a number.");
            waitClick();
            numScanner.nextLine();
            return;
        }
        System.out.print("Enter Product Quantity: ");
        int quantity;
        try {
            quantity = numScanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Quantity must be a number.");
            waitClick();
            numScanner.nextLine();
            return;
        }

        printLastCategories();

        System.out.print("Category Name: ");
        String categoryName = strScanner.nextLine();

        Category category = categoryService.findByName(categoryName);
        if (category != null) {
            UUID categoryId = category.getId();
            if (categoryService.isLast(categoryId)) {
                Product product = new Product(name, price, quantity, categoryId, currentUser.getId());
                try {
                    productService.add(product);
                    System.out.println("Product added successfully!");
                    waitClick();
                } catch (Exception e) {
                    System.out.println("Failed to add product: " + e.getMessage());
                    waitClick();
                }
            } else {
                System.out.println("Category contains subcategories.");
                waitClick();
            }
        } else {
            System.out.println("Category not found.");
            waitClick();
        }
    }

    public static void viewCustomerCartPage() {
        System.out.println("\n--- Your Cart ---");
        Cart cart = cartService.findByCustomerId(currentUser.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            waitClick();
            return;
        } else {
            System.out.println(CartRenderer.render(cart, productService));
        }
        System.out.print("""
                1. Checkout
                2. Remove Item
                
                3. Dashboard
                input %\s""");
        switch (strScanner.nextLine()) {
            case "1" -> checkoutCartPage(cart);
            case "2" -> removeCartItemPage(cart);
            case "3" -> {
            }
        }
    }

    public static void checkoutCartPage(Cart cart) {
        System.out.println("\n--- Checkout Cart ---");
        if (cart == null || cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty, cannot checkout.");
            waitClick();
            return;
        }
        try {
            cartService.checkoutCart(cart, productService);
            System.out.println("Checkout successful! Thank you for your purchase.");
            waitClick();
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
            waitClick();
        }
    }

    public static void removeCartItemPage(Cart cart) {
        System.out.println("\n--- Remove Item from Cart ---");
        if (cart == null || cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            waitClick();
            return;
        }
        System.out.print("Enter product name to remove: ");
        String productName = strScanner.nextLine();
        Product product = productService.findByName(productName);
        if (product == null) {
            System.out.println("Product not found in your cart.");
            waitClick();
            return;
        }
        try {
            cartService.removeItemFromCart(cart, product);
            System.out.println("Item removed successfully!");
            waitClick();
        } catch (Exception e) {
            System.out.println("Failed to remove item: " + e.getMessage());
            waitClick();
        }
    }

    public static void printLastCategories() {
        System.out.println("\n- Available Categories -");
        List<Category> categories = categoryService.getLastCategories();
        System.out.println(CategoryRenderer.render(categories));
    }

    public static void waitClick() {
        strScanner.nextLine();
    }

    public static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
        waitClick();
        initialPage();
    }

}
