package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidCategoryException;
import uz.pdp.model.Category;
import uz.pdp.util.FileUtils;
import uz.pdp.xmlwrapper.CategoryList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryService implements BaseService<Category> {
    private static final String FILE_NAME = "categories.xml";
    List<Category> categories;

    public CategoryService() {
        try {
            categories = readCategories();
        } catch (IOException e) {
            categories = new ArrayList<>();
        }
    }

    @Override
    public void add(Category category) throws IOException, InvalidCategoryException {
        categories = readCategories();
        if (isCategoryValid(category)) {
            categories.add(category);
            save();
        } else {
            throw new InvalidCategoryException("Category with this name already exists or is invalid.");
        }
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
    public boolean update(UUID id, Category category) throws IOException {
        Category found = get(id);
        if (found != null && found.isActive()) {
            found.setName(category.getName());

            save();
            return true;
        }
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        for (Category category : categories) {
            if (category.isActive() && category.getId().equals(id)) {
                category.setActive(false);
                removeChildCategories(category.getId());

                save();
            }
        }
    }

    public void removeChildCategories(UUID categoryId) throws IOException {
        if (categoryId == null) return;
        ArrayList<Category> children = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive() && category.getParentId().equals(categoryId)) {
                children.add(category);
            }
        }
        if (children.isEmpty()) return;
        for (Category category : children) {
            category.setActive(false);
            removeChildCategories(category.getId());
        }
        save();
    }

    private boolean isCategoryValid(Category category) {
        for (Category c : categories) {
            if (c.isActive() && c.getName().equalsIgnoreCase(category.getName())) {
                return false;
            }
        }
        return true;
    }

    public Category getByName(String name) {
        for (Category category : categories) {
            if (category.isActive() && category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }

    public boolean isLast(String name) {
        Category category = getByName(name);
        return isLast(category.getId());
    }

    public boolean isLast(UUID categoryId) {
        for (Category category : categories) {
            if (category.isActive() && category.getParentId().equals(categoryId)) {
                return false;
            }
        }
        return true;
    }

    public List<Category> getAll() {
        List<Category> activeCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive()) {
                activeCategories.add(category);
            }
        }
        return activeCategories;
    }

    private void save() throws IOException {
        CategoryList categoryList = new CategoryList(categories);
        FileUtils.writeToXml(FILE_NAME, categoryList);
    }

    private List<Category> readCategories() throws IOException {
        return FileUtils.readFromXml(FILE_NAME, Category.class);
    }

    public void clear() throws IOException {
        categories.clear();
        save();
    }
}

