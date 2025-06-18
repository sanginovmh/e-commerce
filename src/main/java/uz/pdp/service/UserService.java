package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exception.InvalidUsernameException;
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
            users = readUsers();
        } catch (IOException e) {
            users = new ArrayList<>();
        }
    }

    @Override
    public void add(User user) throws IOException, InvalidUsernameException {
        users = readUsers();
        if (isUsernameValid(user.getUsername())) {
            users.add(user);
            save();
        } else {
            throw new InvalidUsernameException("Username is not valid or already taken.");
        }
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

    /**
     * Logs in a user with the given username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the User object if login is successful, null otherwise
     */
    public User login(String username, String password) {
        for (User user : users) {
            if (user.isActive() && user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User object if found, null otherwise
     */
    public User getByUsername(String username) {
        for (User user : users) {
            if (user.isActive() && user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Checks if a username is valid (not null, not blank, and not already taken).
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    private boolean isUsernameUsed(String username) {
        for (User user : users) {
            if (user.isActive() && user.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates a username based on specific criteria:
     * - Must be at least 3 characters long
     * - Can contain letters, digits, dots, underscores, and hyphens
     * - Must not be blank
     * - Must not already be used by another user
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    public boolean isUsernameValid(String username) {
        return username.matches("^[a-zA-Z0-9._-]{3,}$") && isUsernameUsed(username) && !username.isBlank();
    }

    /**
     * Retrieves a list of all active users.
     *
     * @return a list of active users
     */
    public List<User> getAll() {
        List<User> activeUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }
        return activeUsers;
    }

    /**
     * Initializes the user service by reading users from the XML file.
     *
     * @throws IOException if an I/O error occurs
     */
    private void save() throws IOException {
        UserList userList = new UserList(users);
        FileUtils.writeToXml(FILE_NAME, userList);
    }

    /**
     * Reads the list of users from the XML file.
     *
     * @return a list of users
     * @throws IOException if an I/O error occurs
     */
    private List<User> readUsers() throws IOException {
        return FileUtils.readFromXml(FILE_NAME, User.class);
    }

    /**
     * Clears the user list and saves the empty list to the XML file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void clear() throws IOException {
        users = new ArrayList<>();
        save();
    }


    public String toPrettyStringSuper() {
        StringBuilder sb = new StringBuilder();
        for (User u : getAll()) {
            sb.append(u.getFullName()).append(" - ").append(u.getUsername()).append(" - ").append(u.getRole()).append("\n");
        }
        return sb.toString();
    }
}