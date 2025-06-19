package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidCategoryException;
import uz.pdp.model.Category;
import uz.pdp.util.FileUtils;
import uz.pdp.xmlwrapper.CategoryList;

import java.io.IOException;
import java.util.*;

public class CategoryService implements BaseService<Category> {
    public static final UUID ROOT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String FILE_NAME = "categories.xml";
    List<Category> categories;

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
        for (Category category : categories) {
            if (category.isActive() && category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public List<Category> getAll() {
        List<Category> actives = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive()) {
                actives.add(category);
            }
        }
        return actives;
    }

    @Override
    public boolean update(UUID id, Category category) throws IOException {
        Category existing = get(id);
        if (existing == null || !existing.isActive()) return false;

        existing.setName(category.getName());

        save();
        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Set<UUID> toDeactivate = new HashSet<>();

        collectDecendents(id, toDeactivate);

        for (Category category : categories) {
            if (toDeactivate.contains(category.getId())) {
                category.setActive(false);
            }
        }

        save();
    }

    @Override
    public void clear() throws IOException {
        categories.clear();
        save();
    }

    public List<Category> getDecendents(UUID id) {
        List<Category> children = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive() && category.getParentId().equals(id)) {
                children.add(category);
            }
        }

        return children;
    }

    public List<Category> getLastCategories() {
        Set<UUID> activeParentIds = new HashSet<>();

        for (Category category : categories) {
            if (category.isActive() && !category.getParentId().equals(ROOT_UUID)) {
                activeParentIds.add(category.getParentId());
            }
        }

        List<Category> lastCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive() && !activeParentIds.contains(category.getId())) {
                lastCategories.add(category);
            }
        }

        return lastCategories;
    }

    public Category findByName(String name) {
        String nameLowerCase = name.toLowerCase();
        for (Category category : categories) {
            if (category.isActive() && category.getName()
                    .toLowerCase().equals(nameLowerCase)) {
                return category;
            }
        }
        return null;
    }

    public void collectDecendents(UUID id, Set<UUID> collected) {
        for (Category category : categories) {
            if (category.isActive() && category.getId().equals(id)) {
                collected.add(category.getId());
            }
        }

        for (Category category : categories) {
            if (category.isActive() && collected.contains(category.getParentId())) {
                collected.add(category.getId());
                collectDecendents(category.getId(), collected);
            }
        }
    }

    public void updateCategoryName(Category category, String newName) throws IOException {
        if (get(category.getId()) == null) {
            throw new InvalidCategoryException("Category not found for name: " + category.getName());
        }
        if (findByName(newName) != null) {
            throw new InvalidCategoryException("Category name must be unique.");
        }

        category.setName(newName);

        save();
    }

    public boolean hasSubcategories(UUID id) {
        for (Category category : categories) {
            if (category.isActive() && category.getParentId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void save() throws IOException {
        CategoryList categoryList = new CategoryList(categories);
        FileUtils.writeToXml(FILE_NAME, categoryList);
    }

    private List<Category> loadFromFile() throws IOException {
        return FileUtils.readFromXml(FILE_NAME, Category.class);
    }
}

