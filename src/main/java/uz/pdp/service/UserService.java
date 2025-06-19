package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidUserException;
import uz.pdp.model.User;
import uz.pdp.util.FileUtils;
import uz.pdp.xmlwrapper.UserList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService implements BaseService<User> {
    private static final String FILE_NAME = "users.xml";
    List<User> users;

    public UserService() {
        try {
            users = loadFromFile();
        } catch (IOException e) {
            users = new ArrayList<>();
        }
    }

    @Override
    public void add(User user) throws IOException, InvalidUserException {
        if (!isUsernameValid(user.getUsername())) {
            throw new InvalidUserException("Username is not valid or already taken.");
        }

        users.add(user);

        save();
    }

    @Override
    public User get(UUID id) {
        for (User user : users) {
            if (user.isActive() && user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> activeUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }

        return activeUsers;
    }

    @Override
    public boolean update(UUID id, User user) throws IOException {
        User existing = get(id);
        if (existing == null) {
            return false;
        }

        existing.setFullName(user.getFullName());
        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());

        save();

        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        User existing = get(id);
        if (existing == null) {
            return;
        }

        existing.setActive(false);

        save();
    }

    @Override
    public void clear() throws IOException {
        users = new ArrayList<>();
        save();
    }

    public User login(String username, String password) {
        String usernameLowerCase = username.toLowerCase();
        for (User user : users) {
            if (user.isActive()
                    && user.getUsername().toLowerCase().equals(usernameLowerCase)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User findByUsername(String username) {
        String usernameLowerCase = username.toLowerCase();
        for (User user : users) {
            if (user.isActive()
                    && user.getUsername().toLowerCase().equals(usernameLowerCase)) {
                return user;
            }
        }
        return null;
    }

    public boolean isUsernameValid(String username) {
        return username.matches("^[a-zA-Z0-9._-]{3,12}$")
                && findByUsername(username) == null
                && !username.isBlank();
    }

    private void save() throws IOException {
        UserList userList = new UserList(users);
        FileUtils.writeToXml(FILE_NAME, userList);
    }

    private List<User> loadFromFile() throws IOException {
        return FileUtils.readFromXml(FILE_NAME, User.class);
    }
}