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
            products = readProducts();
        } catch (IOException e) {
            products = new ArrayList<>();
        }
    }

    @Override
    public void add(Product product) throws IOException, IllegalArgumentException {
        products = readProducts();
        if (isProductValid(product)) {
            Product found = getByName(product.getName());
            if (found != null) {
                product.setQuantity(product.getQuantity() + found.getQuantity());
                update(found.getId(), product);
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
    public boolean update(UUID id, Product product) throws IOException {
        Product found = get(id);
        found.setName(product.getName());
        found.setPrice(product.getPrice());
        found.setQuantity(product.getQuantity());
        found.setSellerId(product.getSellerId());
        found.setCategoryId(product.getCategoryId());

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

    public List<Product> getByCategoryId(UUID categoryId) {
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                productList.add(product);
            }
        }
        return productList;
    }

    public Product getByName(String name) {
        for (Product product : products) {
            if (product.isActive() && product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public List<Product> getBySeller(UUID sellerId) {
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

    public boolean isProductValid(Product product) {
        return product.getPrice() > 0 && product.getQuantity() > 0;
    }

    public boolean isCategoryEmpty(UUID categoryId) {
        for (Product p : products) {
            if (p.isActive() && p.getCategoryId().equals(categoryId)) {
                return false;
            }
        }
        return true;
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

    @Override
    public void clear() throws IOException {
        products = new ArrayList<>();
        save();
    }

    public String toPrettyString(List<Product> list) {
        StringBuilder sb = new StringBuilder();
        for (Product product : list) {
            sb.append(product.getName()).append(" - $")
                    .append(product.getPrice()).append(" - quantity: ")
                    .append(product.getQuantity()).append("\n");
        }
        return sb.toString();
    }
}
