package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidUserException;
import uz.pdp.model.User;
import uz.pdp.util.FileUtils;
import uz.pdp.xmlwrapper.UserList;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserService implements BaseService<User> {
    private static final String FILE_NAME = "users.xml";
    private List<User> users;

    private Map<String, User> usersByUsername = new HashMap<>();
    private Map<String, List<User>> usersByFullName = new HashMap<>();

    public UserService() {
        try {
            users = loadFromFile();
        } catch (IOException e) {
            users = new ArrayList<>();
        }

        mapUsersByUsername();
        mapUsersByFullName();
    }

    @Override
    public void add(User user) throws IOException, InvalidUserException {
        if (!isUsernameValid(user.getUsername())) {
            throw new InvalidUserException("Username is not valid or already taken.");
        }

        user.setUsername(user.getUsername().toLowerCase(Locale.ENGLISH));
        user.touch();

        users.add(user);

        save();
    }

    @Override
    public User get(UUID id) {
        return users.stream()
                .filter(u -> u.isActive() && u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAll() {
        return users.stream()
                .filter(User::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
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
        existing.touch();

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
        existing.touch();

        save();
    }

    @Override
    public void clearAndSave() throws IOException {
        users.clear();

        usersByUsername.clear();
        usersByFullName.clear();

        save();
    }

    public User login(String username, String password) {
        String usernameLowerCase = username.toLowerCase();

        User existing = usersByUsername.get(usernameLowerCase);
        if (existing != null && existing.isActive() && existing.getPassword().equals(password)) {
            return existing;
        }
        return null;
    }

    public User findByUsername(String username) {
        String usernameLowerCase = username.toLowerCase();
        return usersByUsername.get(usernameLowerCase);
    }

    public boolean isUsernameValid(String username) {
        return username.matches("^[a-zA-Z0-9._-]{3,12}$")
                && findByUsername(username) == null
                && !username.trim().isEmpty();
    }

    public List<User> searchUsersByUsernameOrFullName(String keyword) {
        List<User> matches = new ArrayList<>();

        User match = usersByUsername.get(keyword);
        if (match != null) {
            matches.add(match);
        }

        List<User> matchesFullName = usersByFullName.get(keyword);
        if (matchesFullName != null && !matchesFullName.isEmpty()) {
            matches.addAll(matchesFullName);
        }

        return matches;
    }

    public User getIgnoreActive(UUID id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void save() throws IOException {
        UserList userList = new UserList(users);
        FileUtils.writeToXml(FILE_NAME, userList);

        mapUsersByUsername();
        mapUsersByFullName();
    }

    private List<User> loadFromFile() throws IOException {
        return FileUtils.readFromXml(FILE_NAME, User.class);
    }

    private void mapUsersByUsername() {
        usersByUsername = users.stream()
                .filter(User::isActive)
                .collect(Collectors.toMap(
                        User::getUsername,
                        Function.identity()
                ));
    }

    private void mapUsersByFullName() {
        usersByFullName = users.stream()
                .filter(User::isActive)
                .collect(Collectors.groupingBy(User::getFullName));
    }
}