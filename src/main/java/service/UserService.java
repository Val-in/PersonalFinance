package service;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private Map<String, User> users;
    private User currentUser;

    public UserService() { this.users = new HashMap<>(); }

    public void registerUser(String username, String password)
    {
        if (users.containsKey(username))
        {
            throw new IllegalArgumentException("User already exists");
        }
        users.put(username, new User(username, password));
    }

    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password");
        }
        currentUser = user;
        return user;
    }
    public User getCurrentUser() { return currentUser; }
    public void logout() { currentUser = null; }
    public boolean isUserLoggedIn() {
       return currentUser != null;
    }
}
