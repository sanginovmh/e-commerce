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
import java.util.*;

public class Alpha {
    static UserService userService = new UserService();
    static CartService cartService = new CartService();
    static ProductService productService = new ProductService();
    static CategoryService categoryService = new CategoryService();

    static Scanner strScanner = new Scanner(System.in);
    static Scanner numScanner = new Scanner(System.in);

    static User currentUser = null;

    public static void main(String[] args) {
        initialPage();
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
            }
        }

        initialPage();
    }

    public static void loginPage() {
        System.out.println("\n--- Login ---");

        System.out.print("Username: ");
        String username = strScanner.nextLine();

        System.out.print("Password: ");
        String password = strScanner.nextLine();

        currentUser = userService.login(username, password);
        if (currentUser == null) {
            System.out.println("Invalid credentials, try again!");
            waitClick();
            initialPage();
            return;
        }

        System.out.println("Login successful!");
        waitClick();

        dashboardPage();
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

        dashboardPage();
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
                case "6" -> renameProductPage();
                case "7" -> logout();

                default -> {
                    System.out.println("Invalid input, try again!");
                    waitClick();
                }
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

                default -> {
                    System.out.println("Invalid input, try again!");
                    waitClick();
                }
            }
        }

        dashboardPage();
    }

    public static void manageUsersPage() {
        System.out.println("\n--- Manage Users ---");

        System.out.println(UserRenderer.render(userService.getAll()));

        System.out.print("Remove user: ");
        String username = strScanner.nextLine();

        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("User not found!");
            waitClick();
            return;
        }

        if (user.getRole().equals(User.UserRole.ADMIN)) {
            System.out.println("Cannot remove an admin!");
            waitClick();
            return;
        }

        try {
            userService.remove(user.getId());
            System.out.println("User removed successfully!");
        } catch (Exception e) {
            System.out.println("Failed to remove user: " + e.getMessage());
        }
        waitClick();
    }

    public static void manageCategoriesPage() {
        System.out.print("""
                \n--- Manage Categories ---
                1. View All Categories
                2. Browse Categories
                3. Create Category
                4. Remove Category
                5. Rename Category
                input %\s""");

        switch (strScanner.nextLine()) {
            case "1" -> displayAllCategoriesPage();
            case "2" -> browseCategories();
            case "3" -> createCategoryPage();
            case "4" -> removeCategoryPage();
            case "5" -> renameCategoryPage();

            default -> {
                System.out.println("Invalid input, try again!");
                waitClick();
                manageCategoriesPage();
            }
        }
    }

    public static void renameCategoryPage() {
        displayAllCategoriesPage();

        System.out.print("Enter category name: ");
        String input = strScanner.nextLine().toLowerCase();

        Category category = categoryService.findByName(input);
        if (category == null) {
            System.out.println("Category not found.");
            waitClick();
            return;
        }

        System.out.print("Enter category new name: ");
        String categoryNewName = strScanner.nextLine();

        try {
            categoryService.updateCategoryName(category, categoryNewName);
            System.out.println("Category name changed successfully.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (InvalidCategoryException e) {
            System.out.println("Category exception: " + e.getMessage());
        }
        waitClick();
    }

    public static void displayAllCategoriesPage() {
        System.out.println("\n--- All Categories ---");

        List<Category> categories = categoryService.getAll();

        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            waitClick();
            return;
        }

        System.out.println(CategoryRenderer.render(categories));
        waitClick();
    }

    public static void renameProductPage() {
        viewYourProductsPage();

        System.out.print("Enter product name: ");
        String input = strScanner.nextLine().toLowerCase();

        Product product = productService.findByName(input);
        if (product == null) {
            System.out.println("Product not found.");
            waitClick();
            return;
        }

        System.out.print("Enter product new name: ");
        String productNewName = strScanner.nextLine();

        try {
            productService.updateProductName(product, productNewName);
            System.out.println("Product name changed successfully.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (InvalidProductException e) {
            System.out.println("Product exception: " + e.getMessage());
        }
        waitClick();
    }

    public static void browseCategories() {
        System.out.println("\n--- Browse ---");

        Category root = new Category("Root", null);
        Deque<Category> path = new ArrayDeque<>();
        path.push(root);

        while (true) {
            Category current = path.peek();
            if (current == null) {
                System.out.println("Something went wrong.");
                waitClick();
                return;
            }

            UUID currentId = current.getId();
            if (current.getParentId() == null) {
                currentId = CategoryService.ROOT_UUID;
            }


            List<Category> children = categoryService.getDecendents(currentId);
            System.out.println("\n- " + current.getName() + " -");

            if (!children.isEmpty()) {
                System.out.println(CategoryRenderer.render(children));
            } else {
                System.out.println("[no subcategories]");
                waitClick();
            }

            System.out.print("Go to ('.' to go back): ");
            String input = strScanner.nextLine().toLowerCase();

            if (input.trim().equals(".")) {
                if (path.size() > 1) {
                    path.pop();
                } else {
                    return;
                }
                continue;
            }

            Category selected = categoryService.findByName(input);
            if (selected == null) {
                System.out.println("Selected category not found.");
                waitClick();
                continue;
            }

            if (categoryService.hasSubcategories(selected.getId())) {
                path.push(selected);
            } else {
                productsDisplayPage(selected);
            }
        }
    }

    public static void productsDisplayPage(Category current) {
        System.out.println("\n- " + current.getName() + " -");

        List<Product> products = productService.getByCategoryId(current.getId());
        if (products.isEmpty()) {
            System.out.println("[no products yet]");
            waitClick();
            return;
        }

        System.out.println(ProductRenderer.render(products));

        if (currentUser.getRole() != User.UserRole.CUSTOMER) {
            waitClick();
            return;
        }

        selectProductPage();
    }

    public static void selectProductPage() {
        System.out.print("Select: ");
        String input = strScanner.nextLine();

        if (input.trim().equals(".")) {
            return;
        }

        Product selected = productService.findByName(input);
        if (selected == null) {
            System.out.println("Selected product not found.");
            waitClick();
            return;
        }

        customerProductMenu(selected);
    }

    public static void customerProductMenu(Product product) {
        System.out.println("\n--- Product Menu ---");

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
            } catch (Exception e) {
                System.out.println("Unable to create cart: " + e.getMessage());
            }
            waitClick();
        }

        try {
            cartService.addItemToCart(userId, product, quantity);
            System.out.println("Item added to cart!");
        } catch (Exception e) {
            System.out.println("Unable to add product to cart: " + e.getMessage());
        }
        waitClick();
    }

    public static void createCategoryPage() {
        System.out.println("\n--- Add New Category ---");

        System.out.print("Category Name: ");
        String name = strScanner.nextLine();

        if (name.trim().equalsIgnoreCase("root")) {
            System.out.println("Category name cannot be 'root'.");
            waitClick();
            return;
        }

        printLastCategories();

        System.out.print("Parent Category (or 'Root'): ");
        String parentName = strScanner.nextLine().toLowerCase();

        UUID parentId = CategoryService.ROOT_UUID;


        if (!parentName.equalsIgnoreCase("root")) {
            Category parent = categoryService.findByName(parentName);
            if (parent == null) {
                System.out.println("Parent category not found.");
                waitClick();
                return;
            }

            if (!productService.isCategoryEmpty(parentId)) {
                System.out.println("Parent category must be empty of products.");
                waitClick();
                return;
            }

            parentId = parent.getId();
        }

        Category newCategory = new Category(name, parentId);
        try {
            categoryService.add(newCategory);
            System.out.println("Category added successfully!");
        } catch (Exception e) {
            System.out.println("Failed to add category: " + e.getMessage());
        }
        waitClick();
    }

    public static void removeCategoryPage() {
        System.out.println("\n--- Remove Category ---");

        System.out.print("Category Name: ");
        String name = strScanner.nextLine();

        Category category = categoryService.findByName(name);
        if (category == null) {
            System.out.println("Category not found.");
            waitClick();
            return;
        }

        try {
            categoryService.remove(category.getId());
            System.out.println("Category removed successfully!");
        } catch (Exception e) {
            System.out.println("Error removing category: " + e.getMessage());
        }
        waitClick();
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
            return;
        }

        ProductRenderer.render(products);
        waitClick();
    }

    public static void removeProductPage() {
        System.out.println("\n--- Remove Product ---");

        System.out.print("Product Name: ");
        String name = strScanner.nextLine();

        Product product = productService.findByName(name);
        if (product == null) {
            System.out.println("Product not found.");
            waitClick();
            return;
        }

        try {
            productService.remove(product.getId());
            System.out.println("Product removed successfully!");
        } catch (Exception e) {
            System.out.println("Failed to remove product: " + e.getMessage());
        }
        waitClick();
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
            return;
        }

        System.out.println(CartRenderer.adminRender(carts, userService, productService));
        waitClick();
    }

    public static void removeCartPage() {
        System.out.println("\n--- Remove Cart ---");

        System.out.print("Customer Username: ");
        String username = strScanner.nextLine();

        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("User not found.");
            waitClick();
            return;
        }

        try {
            cartService.removeByCustomerId(user.getId());
            System.out.println("Cart removed successfully!");
        } catch (Exception e) {
            System.out.println("Failed to remove cart: " + e.getMessage());
        }
        waitClick();
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
        } catch (Exception e) {
            System.out.println("Failed to create admin: " + e.getMessage());
        }
        waitClick();
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
            return;
        }

        System.out.println(ProductRenderer.render(products));
        waitClick();
    }

    public static void removeSellerProductPage() {
        System.out.println("\n--- Remove Your Product ---");

        System.out.print("Product Name: ");
        String name = strScanner.nextLine();

        Product product = productService.findByName(name);
        if (product == null) {
            System.out.println("Product not found.");
            waitClick();
            return;
        }
        if (!product.getSellerId().equals(currentUser.getId())) {
            System.out.println("You are not the product seller.");
            waitClick();
            return;
        }

        try {
            productService.remove(product.getId());
            System.out.println("Product removed successfully!");
        } catch (Exception e) {
            System.out.println("Failed to remove product: " + e.getMessage());
        }
        waitClick();
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
        if (category == null) {
            System.out.println("Category not found.");
            waitClick();
            return;
        }

        UUID categoryId = category.getId();
        if (categoryService.hasSubcategories(categoryId)) {
            System.out.println("Category contains subcategories.");
            waitClick();
            return;
        }

        Product product = new Product(name, price, quantity, categoryId, currentUser.getId());
        try {
            productService.add(product);
            System.out.println("Product added successfully!");
        } catch (Exception e) {
            System.out.println("Failed to add product: " + e.getMessage());
        }
        waitClick();
    }

    public static void viewCustomerCartPage() {
        System.out.println("\n--- Your Cart ---");

        Cart cart = cartService.findByCustomerId(currentUser.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            waitClick();
            return;
        }

        System.out.println(CartRenderer.render(cart, productService));

        System.out.print("""
                1. Checkout
                2. Remove Item
                
                3. Dashboard
                input %\s""");

        switch (strScanner.nextLine()) {
            case "1" -> checkoutCartPage(cart);
            case "2" -> removeCartItemPage(cart);
        }
    }

    public static void checkoutCartPage(Cart cart) {
        System.out.println("\n--- Checkout Cart ---");

        if (cart == null || cart.getItems().isEmpty()) {
            System.out.println("Cannot checkout because the cart is empty.");
            waitClick();
            return;
        }

        try {
            cartService.checkoutCart(cart, productService);
            System.out.println("Checkout successful! Thank you for your purchase.");
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
        waitClick();
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
        } catch (Exception e) {
            System.out.println("Failed to remove item: " + e.getMessage());
        }
        waitClick();
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
