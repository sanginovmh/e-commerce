package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidProductException;
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
            products = loadFromFile();
        } catch (IOException e) {
            products = new ArrayList<>();
        }
    }

    @Override
    public void add(Product product) throws IOException, IllegalArgumentException {
        if (product.getPrice() > 0 && product.getQuantity() > 0) {
            Product found = findByName(product.getName());
            if (found != null) {
                found.setQuantity(product.getQuantity() + found.getQuantity());
            } else {
                products.add(product);
            }

            save();
        } else {
            throw new InvalidProductException("Invalid product parameters.");
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
    public List<Product> getAll() {
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive()) {
                productList.add(product);
            }
        }

        return productList;
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

        save();
        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Product found = get(id);
        if (found != null && found.isActive()) {
            found.setActive(false);

            save();
        }
    }

    @Override
    public void clear() throws IOException {
        products = new ArrayList<>();
        save();
    }

    public List<Product> getByCategoryId(UUID categoryId) {
        List<Product> categoryProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                categoryProducts.add(product);
            }
        }

        return categoryProducts;
    }

    public List<Product> getBySeller(UUID sellerId) {
        List<Product> sellerProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive() && product.getSellerId().equals(sellerId)) {
                sellerProducts.add(product);
            }
        }

        return sellerProducts;
    }

    public Product findByName(String name) {
        String nameLowerCase = name.toLowerCase();
        for (Product product : products) {
            if (product.isActive() && product.getName()
                    .toLowerCase().equals(nameLowerCase)) {
                return product;
            }
        }
        return null;
    }

    public boolean isCategoryEmpty(UUID categoryId) {
        for (Product p : products) {
            if (p.isActive() && p.getCategoryId().equals(categoryId)) {
                return false;
            }
        }
        return true;
    }

    public void purchaseProducts(
            UUID productId,
            int quantity
    ) throws IOException,
            InvalidProductException {
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

        save();
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, products);
    }

    private List<Product> loadFromFile() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Product.class);
    }
}
