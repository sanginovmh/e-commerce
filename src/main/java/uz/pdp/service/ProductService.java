package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidProductException;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.util.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductService implements BaseService<Product> {
    private static final String FILE_NAME = "products.json";
    List<Product> products;

    public ProductService() {
        try {
            products = readProducts();
        } catch (IOException e) {
            products = new ArrayList<>();
        }
    }

    @Override
    public void add(Product product) throws IOException, IllegalArgumentException {
        products = readProducts();
        if (isProductValid(product)) {
            products.add(product);
            save();
        } else {
            Product existingProduct = getByName(product.getName());
            if (existingProduct == null) {
                throw new InvalidProductException("Product with name " + product.getName() + "must have positive parameters.");
            }
            product.setQuantity(existingProduct.getQuantity() + product.getQuantity());
            update(existingProduct.getId(), product);
        }
    }

    @Override
    public Product get(UUID id) {
        for (Product product : products) {
            if (product.isActive() && product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public boolean update(UUID id, Product product) throws IOException {
        Product found = get(id);
        if (found != null && found.isActive()) {
            found.setName(product.getName());
            found.setPrice(product.getPrice());
            found.setQuantity(product.getQuantity());
            found.setSellerId(product.getSellerId());
            found.setCategoryId(product.getCategoryId());

            save();
            return true;
        }
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Product found = get(id);
        if (found != null && found.isActive()) {
            found.setActive(false);
            save();
        }
    }

    public List<Product> getByCategory(String categoryName, CategoryService categoryService) {
        UUID categoryId = categoryService.getByName(categoryName).getId();
        if (categoryService.isLast(categoryName)) {
            List<Product> productList = new ArrayList<>();
            for (Product product : products) {
                if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                    productList.add(product);
                }
            }
            return productList;
        }
        return null;
    }

    public Product getByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> getBySeller(String username, UserService userService) {
        UUID sellerId = userService.getByUsername(username).getId();
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive() && product.getSellerId().equals(sellerId)) {
                productList.add(product);
            }
        }
        return productList;
    }

    public List<Product> getAll() {
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive()) {
                productList.add(product);
            }
        }
        return productList;
    }

    public List<Category> getPath(String productName, CategoryService categoryService) {
        UUID categoryId = getByName(productName).getCategoryId();
        List<Category> path = new ArrayList<>();
        while (true) {
            Category category = categoryService.get(categoryId);
            if (category == null || !category.isActive()) {
                break;
            }
            path.add(category);
            categoryId = category.getParentId();
        }
        return path;
    }

    public boolean isProductValid(Product product) {
        for (Product p : products) {
            if (p.isActive() && p.getName().equalsIgnoreCase(product.getName())) {
                return false;
            }
        }
        return true;
    }

    public void deleteProductsByCategory(UUID categoryId) throws IOException {
        for (Product product : products) {
            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                product.setActive(false);
            }
        }
        save();
    }

    public void buyProduct(UUID productId, int quantity) throws IOException, InvalidProductException {
        Product product = get(productId);
        if (product == null || !product.isActive()) {
            throw new InvalidProductException("Product not found or inactive.");
        }
        product.setQuantity(product.getQuantity() - quantity);
        if (product.getQuantity() == 0) {
            product.setActive(false);
        }
        save();
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, products);
    }

    private List<Product> readProducts() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Product.class);
    }

    public void clear() throws IOException {
        products = new ArrayList<>();
        save();
    }
}
