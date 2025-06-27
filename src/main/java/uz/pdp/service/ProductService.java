package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidProductException;
import uz.pdp.model.Product;
import uz.pdp.util.FileUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProductService implements BaseService<Product> {
    private static final String FILE_NAME = "products.json";
    private List<Product> products;

    private Map<UUID, Product> productsById = new HashMap<>();
    private Map<UUID, List<Product>> productsByCategoryId = new HashMap<>();

    public ProductService() {
        try {
            products = loadFromFile();
        } catch (IOException e) {
            products = new ArrayList<>();
        }

        mapProductsById();
        mapProductsByCategoryId();
    }

    @Override
    public void add(Product product) throws IOException, IllegalArgumentException {
        if (product.getPrice() <= 0 || product.getQuantity() <= 0) {
            throw new InvalidProductException("Product parameters must be positive.");
        }

        Product existing = findByName(product.getName());
        if (existing == null) {
            products.add(product);
        } else {
            existing.setQuantity(product.getQuantity() + existing.getQuantity());
            existing.touch();
        }

        save();
    }

    @Override
    public Product get(UUID id) {
        return productsById.get(id);
    }

    @Override
    public List<Product> getAll() {
        return products.stream()
                .filter(Product::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean update(UUID id, Product product) throws IOException {
        Product existing = get(id);
        if (existing == null) return false;

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setSellerId(product.getSellerId());
        existing.setCategoryId(product.getCategoryId());
        existing.touch();

        save();
        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Product existing = get(id);
        if (existing != null && existing.isActive()) {
            existing.setActive(false);
            existing.touch();

            save();
        }
    }

    @Override
    public void clearAndSave() throws IOException {
        products.clear();

        productsById.clear();
        productsByCategoryId.clear();

        save();
    }

    public List<Product> getByCategoryId(UUID id) {
        return productsByCategoryId.get(id);
    }

    public List<Product> getBySellerId(UUID id) {
        return products.stream()
                .filter(p -> p.isActive() && p.getSellerId().equals(id))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Product findByName(String name) {
        Predicate<Product> matchesName = p -> p.getName().equalsIgnoreCase(name);

        return products.stream()
                .filter(Product::isActive)
                .filter(matchesName)
                .findFirst()
                .orElse(null);
    }

    public boolean isCategoryEmpty(UUID id) {
        return products.stream()
                .filter(Product::isActive)
                .noneMatch(p -> p.getCategoryId().equals(id));
    }

    public void updateProductName(Product product, String newName) throws IOException {
        if (get(product.getId()) == null) {
            throw new InvalidProductException("Product not found for name: " + product.getName());
        }
        if (findByName(newName) != null) {
            throw new InvalidProductException("Product name must be unique.");
        }

        product.setName(newName);
        product.touch();

        save();
    }

    public void purchaseProducts(UUID productId, int quantity)
            throws IOException, InvalidProductException {
        Product product = get(productId);
        if (product == null || !product.isActive()) {
            throw new InvalidProductException("Product not found or inactive.");
        }
        if (quantity <= 0) {
            throw new InvalidProductException("Quantity must be positive.");
        }
        if (quantity > product.getQuantity()) {
            throw new InvalidProductException("Insufficient stock.");
        }

        product.setQuantity(product.getQuantity() - quantity);

        if (product.getQuantity() == 0) {
            product.setActive(false);
        }
        product.touch();

        save();
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, products);
    }

    private List<Product> loadFromFile() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Product.class);
    }

    private void mapProductsById() {
        productsById = products.stream()
                .filter(Product::isActive)
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));
    }

    private void mapProductsByCategoryId() {
        productsByCategoryId = products.stream()
                .filter(Product::isActive)
                .collect(Collectors.groupingBy(Product::getCategoryId));
    }
}
