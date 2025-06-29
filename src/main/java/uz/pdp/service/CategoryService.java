package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidCategoryException;
import uz.pdp.model.Category;
import uz.pdp.util.FileUtils;
import uz.pdp.xmlwrapper.CategoryList;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CategoryService implements BaseService<Category> {
    private static final String FILE_NAME = "categories.xml";
    public static final UUID ROOT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private List<Category> categories;

    public CategoryService() {
        try {
            categories = loadFromFile();
        } catch (IOException e) {
            categories = new ArrayList<>();
        }
    }

    @Override
    public void add(Category category) throws IOException, InvalidCategoryException {
        if (findByName(category.getName()) != null) {
            throw new InvalidCategoryException("Category with this name already exists or is invalid.");
        }

        categories.add(category);

        save();
    }

    @Override
    public Category get(UUID id) {
        return categories.stream()
                .filter(Category::isActive)
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Category> getAll() {
        return categories.stream()
                .filter(Category::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean update(UUID id, Category category) throws IOException {
        Category existing = get(id);
        if (existing == null || !existing.isActive()) return false;

        existing.setName(category.getName());
        existing.touch();

        save();
        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Set<UUID> toDeactivate = new HashSet<>();

        collectDescendants(id, toDeactivate);

        categories.stream()
                .filter(c -> toDeactivate.contains(c.getId()))
                .forEach(c -> {
                    c.setActive(false);
                    c.touch();
                });

        save();
    }

    @Override
    public void clearAndSave() throws IOException {
        categories.clear();
        save();
    }

    public List<Category> getDescendants(UUID id) {
        return categories.stream()
                .filter(Category::isActive)
                .filter(c -> c.getParentId().equals(id))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Category> getLastCategories() {
        Set<UUID> parentIds = categories.stream()
                .filter(Category::isActive)
                .map(Category::getParentId)
                .filter(parentId -> !parentId.equals(ROOT_UUID))
                .collect(Collectors.toSet());

        return categories.stream()
                .filter(Category::isActive)
                .filter(c -> !parentIds.contains(c.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Category> getCategoriesEmptyOfProducts(Predicate<Category> isEmptyOfProducts) {
        return categories.stream()
                .filter(isEmptyOfProducts)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Category findByName(String name) {
        String nameLowerCase = name.toLowerCase();

        return categories.stream()
                .filter(Category::isActive)
                .filter(c -> c.getName().toLowerCase().equals(nameLowerCase))
                .findFirst()
                .orElse(null);
    }

    private void collectDescendants(UUID id, Set<UUID> collected) {
        if (categories.stream().noneMatch(c -> c.getId().equals(id))) return;

        collected.add(id);
        categories.stream()
                .filter(Category::isActive)
                .filter(c -> c.getParentId().equals(id))
                .forEach(c -> collectDescendants(c.getId(), collected));
    }

    public void updateCategoryName(Category category, String newName) throws IOException {
        if (get(category.getId()) == null) {
            throw new InvalidCategoryException("Category not found for name: " + category.getName());
        }
        if (findByName(newName) != null) {
            throw new InvalidCategoryException("Category name must be unique.");
        }

        category.setName(newName);
        category.touch();

        save();
    }

    public boolean hasDescendants(UUID id) {
        return categories.stream()
                .filter(Category::isActive)
                .anyMatch(c -> c.getParentId().equals(id));
    }

    private void save() throws IOException {
        CategoryList categoryList = new CategoryList(categories);
        FileUtils.writeToXml(FILE_NAME, categoryList);
    }

    private List<Category> loadFromFile() throws IOException {
        return FileUtils.readFromXml(FILE_NAME, Category.class);
    }
}