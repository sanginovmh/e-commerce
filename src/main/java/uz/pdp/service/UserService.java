package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidUserException;
import uz.pdp.model.User;
import uz.pdp.util.FileUtils;
import uz.pdp.xmlwrapper.UserList;

import java.io.IOException;
import java.util.*;

public class UserService implements BaseService<User> {
    private static final String FILE_NAME = "users.xml";
    Map<UUID, User> usersByUuid;

    public UserService() {
        try {
            usersByUuid = loadFromFile();
        } catch (IOException e) {
            usersByUuid = new HashMap<>();
        }
    }

    @Override
    public void add(User user) throws IOException, InvalidUserException {
        if (isUsernameValid(user.getUsername())) {
            usersByUuid.put(user.getId(), user);
            save();
        } else {
            throw new InvalidUserException("Username is not valid or already taken.");
        }
    }

    @Override
    public User get(UUID id) {
        return usersByUuid.get(id);
    }

    @Override
    public List<User> getAll() {
        List<User> activeUsers = new ArrayList<>();
        for (User user : usersByUuid.values()) {
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }
        return activeUsers;
    }

    @Override
    public boolean update(UUID id, User user) throws IOException {
        User found = get(id);
        if (found != null) {
            found.setFullName(user.getFullName());
            found.setUsername(user.getUsername());
            found.setPassword(user.getPassword());
            found.setRole(user.getRole());

            save();
            return true;
        }
        return true;
    }

    @Override
    public void remove(UUID id) throws IOException {
        User found = get(id);
        if (found != null) {
            found.setActive(false);

            save();
        }
    }

    @Override
    public void clear() throws IOException {
        usersByUuid = new HashMap<>() {
        };
        save();
    }

    public User login(String username, String password) {
        String usernameLowerCase = username.toLowerCase();
        for (User user : usersByUuid.values()) {
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
        for (User user : usersByUuid.values()) {
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
        List<User> users = new ArrayList<>(usersByUuid.values());
        UserList userList = new UserList(users);
        FileUtils.writeToXml(FILE_NAME, userList);
    }

    private Map<UUID, User> loadFromFile() throws IOException {

        Map<UUID, User> userMap = new HashMap<>();
        List<User> users = FileUtils.readFromJson(FILE_NAME, User.class);
        if (users != null) {
            for (User user : users) {
                userMap.put(user.getId(), user);
            }
        }
        return userMap;
    }
}