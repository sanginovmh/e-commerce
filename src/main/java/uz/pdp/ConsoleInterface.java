package uz.pdp;

import uz.pdp.exception.InvalidCategoryException;
import uz.pdp.exception.InvalidOrderException;
import uz.pdp.exception.InvalidProductException;
import uz.pdp.renderer.*;
import uz.pdp.model.*;
import uz.pdp.service.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class ConsoleInterface {
    static UserService userService = new UserService();
    static CartService cartService = new CartService();
    static ProductService productService = new ProductService();
    static CategoryService categoryService = new CategoryService();
    static OrderService orderService = new OrderService();

    static Scanner strScanner = new Scanner(System.in);
    static Scanner numScanner = new Scanner(System.in);

    static User currentUser = null;

    public static void main(String[] args) {
        initialPage();
    }

    @SuppressWarnings("InfiniteRecursion")
    public static void initialPage() {
        System.out.println("--- Welcome to Alpha! ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit\n\n");
        System.out.println("input % ");

        switch (strScanner.nextLine()) {
            case "1":
                loginPage();
                break;

            case "2":
                registerPage();
                break;
            case "3":
                System.exit(0);
                break;

            default:
                System.out.println("Invalid input, try again!");
                waitClick();
                break;
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
            case "C":
                role = User.UserRole.CUSTOMER;
                break;
            case "S":
                role = User.UserRole.SELLER;
                break;

            default:
                System.out.println("Invalid role, try again!");
                waitClick();
                registerPage();
                return;
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

    @SuppressWarnings("InfiniteRecursion")
    public static void dashboardPage() {
        if (currentUser.getRole().equals(User.UserRole.ADMIN)) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Categories");
            System.out.println("3. Manage Products");
            System.out.println("4. Manage Carts\n");

            System.out.println("5. Browse Categories");
            System.out.println("6. Create New Admin\n");

            System.out.println("7. Search Super");
            System.out.println("8. Search Global");
            System.out.println("9. View Orders\n");

            System.out.println("10. Logout");
            System.out.println("0. Exit\n\n");

            System.out.print("input % ");

            switch (strScanner.nextLine()) {
                case "1":
                    manageUsersPage();
                    break;
                case "2":
                    manageCategoriesPage();
                    break;
                case "3":
                    manageProductsPage();
                    break;
                case "4":
                    manageCartsPage();
                    break;
                case "5":
                    browseCategories();
                    break;
                case "6":
                    createNewAdminPage();
                    break;
                case "7":
                    searchSuperPage();
                    break;
                case "8":
                    searchDash();
                    break;
                case "9":
                    viewAllOrders();
                    break;
                case "10":
                    logout();
                    break;
                case "0":
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid input, try again!");
                    waitClick();
                    break;
            }
        } else if (currentUser.getRole().equals(User.UserRole.SELLER)) {
            System.out.println("\n-- - Seller Dashboard-- -");
            System.out.println("1. Browse Products");
            System.out.println("2. View Your Products");
            System.out.println("3. Remove Product");
            System.out.println("4. Add New Product");
            System.out.println("5. Search Global");
            System.out.println("6. Change Your Product Name\n");

            System.out.println("7. Logout\n\n");

            System.out.println("input % ");

            switch (strScanner.nextLine()) {
                case "1":
                    browseCategories();
                    break;
                case "2":
                    viewYourProductsPage();
                    break;
                case "3":
                    removeSellerProductPage();
                    break;
                case "4":
                    addNewProductPage();
                    break;
                case "5":
                    searchDash();
                    break;
                case "6":
                    renameProductPage();
                    break;
                case "7":
                    logout();
                    break;

                default:
                    System.out.println("Invalid input, try again!");
                    waitClick();
                    break;

            }
        } else if (currentUser.getRole().equals(User.UserRole.CUSTOMER)) {
            System.out.print("\n-- - Customer Dashboard-- -");
            System.out.println("1. Browse");
            System.out.println("2. View Your Cart");
            System.out.println("3. View Previous Orders");
            System.out.println("4. Search Global\n");

            System.out.println("5. Logout\n\n");

            System.out.println("input % ");
            switch (strScanner.nextLine()) {
                case "1":
                    browseCategories();
                    break;
                case "2":
                    viewCustomerCartPage();
                    break;
                case "3":
                    viewCustomerOrdersPage();
                    break;
                case "4":
                    searchDash();
                    break;
                case "5":
                    logout();
                    break;

                default:
                    System.out.println("Invalid input, try again!");
                    waitClick();
                    break;

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
        System.out.print("\n-- - Manage Categories-- -");
        System.out.println(" 1. View All Categories");
        System.out.println(" 2. Browse Categories");
        System.out.println(" 3. Create Category");
        System.out.println(" 4. Remove Category");
        System.out.println(" 5. Rename Category\n\n");
        System.out.println(" input % ");

        switch (strScanner.nextLine()) {
            case "1":
                displayAllCategoriesPage();
                break;
            case "2":
                browseCategories();
                break;
            case "3":
                createCategoryPage();
                break;
            case "4":
                removeCategoryPage();
                break;
            case "5":
                renameCategoryPage();
                break;

            default:
                System.out.println("Invalid input, try again!");
                waitClick();
                manageCategoriesPage();
                break;
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

            List<Category> children = categoryService.getDescendants(currentId);
            System.out.println("\n- " + current.getName() + " -");

            if (!children.isEmpty()) {
                System.out.println(CategoryRenderer.render(children));
            } else {
                System.out.println("[no subcategories]");
                waitClick();
            }

            System.out.print("Go to ('.' to go back): ");
            String input = strScanner.nextLine();

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

            if (categoryService.hasDescendants(selected.getId())) {
                path.push(selected);
            } else {
                productsDisplayPage(selected);
            }
        }
    }

    public static void productsDisplayPage(Category current) {
        System.out.println("\n- " + current.getName() + " -");

        List<Product> products = productService.getByCategoryId(current.getId());
        if (products == null || products.isEmpty()) {
            System.out.print("[no products yet]");
            waitClick();
            return;
        }

        System.out.print(ProductRenderer.render(products));

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
        System.out.println ("     1. Add Cart ");
        System.out.println ("     2. Back to Categories ");
        System.out.println ("     3. Back to Dashboard ");


        switch (strScanner.nextLine()) {
            case "1" :
                addToCartPage(product);
                break;
            case "3" :
                dashboardPage();
                break;
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

        printCategoriesEmptyOfProducts();

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
        System.out.println("\n---- Manage Products ----");
        System.out.println("       1.View All Products ");
        System.out.println("       2.Remove Product ");


        switch (strScanner.nextLine()) {
            case "1" :
                displayAllProducts();
                break;
            case "2" :
                removeProductPage();
                break;

            default :
                System.out.println("Invalid input, try again!");
                waitClick();

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
        System.out.println("\n---- Manage Carts ----");
        System.out.println("      1. View All Carts ");
        System.out.println("      2. Remove Cart ");


        switch (strScanner.nextLine()) {
            case "1" :
                viewAllCarts();
                break;
            case "2" :
                removeCartPage();
                break;

            default :
                System.out.println("Invalid input, try again!");
                waitClick();
                manageCartsPage();

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
        // TODO implement search across users, products, and categories all together
    }

    public static void searchDash() {
        // TODO let user choose what to search for (users, products)
    }

    public static void searchUsers() {
        System.out.println("\n--- Search Users ---");

        System.out.print("Enter search term: ");
        String keyword = strScanner.nextLine();

        List<User> usersMatched = userService.searchUsersByUsernameOrFullName(keyword);
        if (usersMatched.isEmpty()) {
            System.out.println("[no users found]");
            waitClick();
            return;
        }

        System.out.print(UserRenderer.render(usersMatched));

        // TODO if ADMIN, let interact with chosen user
    }

    public static void viewAllOrders() {
        System.out.println("\n--- All Orders ---");

        List<Order> orders = orderService.getAll();
        if (orders.isEmpty()) {
            System.out.println("[no orders yet]");
            waitClick();
            return;
        }

        System.out.println(OrderRenderer.render(orders));
        waitClick();
    }

    public static void viewCustomerOrdersPage() {
        System.out.println("\n--- Previous Orders ---");

        List<Order> orders = orderService.getByCustomerId(currentUser.getId());
        if (orders.isEmpty()) {
            System.out.println("[no orders yet]");
            waitClick();
            return;
        }

        System.out.println(OrderRenderer.render(orders));
        waitClick();
    }

    public static void viewYourProductsPage() {
        System.out.println("\n--- Your Products ---");

        List<Product> products = productService.getBySellerId(currentUser.getId());
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
        if (categoryService.hasDescendants(categoryId)) {
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

        System.out.println(" 1. Checkout ");
        System.out.println(" 2. Remove Item ");
        System.out.println(" 3. Dashboard ");

        switch (strScanner.nextLine()) {
            case "1" :
                checkoutCartPage(cart);
                break;
            case "2" :
                removeCartItemPage(cart);
                break;
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
            if (cartService.isInvalidAndSanitizeIfTrue(cart, productService::get)) {
                System.out.println("Product is out of stock or quantity is invalid. Item removed from cart.");
                waitClick();
                return;
            }

            cartService.checkoutCart(cart, productService::purchaseProducts);

            createNewOrder(cart);

            System.out.println("Checkout successful! Thank you for your purchase.");
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
        waitClick();
    }

    public static void createNewOrder(Cart cart) {
        try {
            Order order = orderService.buildNewOrder(
                    cart,
                    currentUser,
                    userService::getIgnoreActive,
                    productService::get
            );

            orderService.add(order);
            System.out.print("Order created successfully.");
        } catch (IOException e) {
            System.out.print("Error reading file: " + e.getMessage());
        } catch (InvalidOrderException e) {
            System.out.print("Could not create order: " + e.getMessage());
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

    public static void printCategoriesEmptyOfProducts() {
        System.out.println("\n- Available Categories -");

        Predicate<Category> isEmptyOfProducts = category -> productService.isCategoryEmpty(category.getId());
        List<Category> categories = categoryService.getCategoriesEmptyOfProducts(isEmptyOfProducts);

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