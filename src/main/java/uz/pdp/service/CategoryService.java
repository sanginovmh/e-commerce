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
    public static final UUID ROOT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
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
                killChildren(category.getId());

                save();
            }
        }
    }

    public void killChildren(UUID categoryId) throws IOException {
        Category category = get(categoryId);
        if (category == null) {
            return;
        }
        ArrayList<Category> children =
                (ArrayList<Category>) getChildren(category.getName());
        if (children.isEmpty()) {
            return;
        }
        for (Category child : children) {
            child.setActive(false);
            killChildren(child.getId());
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

    public boolean isLast(UUID categoryId) {
        for (Category c : categories) {
            if (c.isActive() && c.getParentId().equals(categoryId)) {
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

    public List<Category> getChildren(String name) {
        List<Category> children = new ArrayList<>();
        UUID categoryId;
        if (name.equals("Root")) {
            categoryId = ROOT_UUID;
        } else {
            Category category = getByName(name);
            if (category == null) {
                return children;
            }
            categoryId = category.getId();
        }
        for (Category child : categories) {
            if (child.isActive() && child.getParentId().equals(categoryId)) {
                children.add(child);
            }
        }
        return children;
    }

    public List<Category> getChildren(UUID categoryId) {
        List<Category> children = new ArrayList<>();
        for (Category child : categories) {
            if (child.isActive() && child.getParentId().equals(categoryId)) {
                children.add(child);
            }
        }
        return children;
    }

    public List<Category> getUncles(UUID categoryId) {
        Category category = get(categoryId);
        Category parent = get(category.getParentId());
        if (parent != null) {
            return getChildren(parent.getId());
        }
        return null;
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

    public String toPrettyString(List<Category> list) {
        StringBuilder sb = new StringBuilder();
        for (Category c : list) {
            sb.append(c.getName()).append("\n");
        }
        return sb.toString();
    }
}

