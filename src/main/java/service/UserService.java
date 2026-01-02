package service;

import exception.InvalidCredentialsException;
import exception.UserNotFoundException;
import storage.FileUserStorage;
import storage.FileWalletStorage;
import model.User;
import model.Wallet;
import storage.UserStorage;
import storage.WalletStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private Map<String, User> users;
    private User currentUser;
    private final WalletStorage walletStorage;
    private final UserStorage userStorage;


    public UserService() {
        this.walletStorage = new FileWalletStorage();
        this.userStorage = new FileUserStorage();
        this.users = userStorage.loadUsers(); }

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
        Wallet wallet = walletStorage.load(username);
        user.setWallet(wallet);
        currentUser = user;
        return user;
    }
    public User getCurrentUser() { return currentUser; }

    public boolean isUserLoggedIn() {
       return currentUser != null;
    }

    public void logout() {
        if (currentUser != null) {
            walletStorage.save(currentUser.getUsername(), currentUser.getWallet());
            userStorage.saveUsers(users); // сохраняем всех пользователей
            currentUser = null;
        }
    }
    public void exportUsers(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка экспорта", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void importUsers(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Map<String, User> imported = (Map<String, User>) in.readObject();
            users.putAll(imported);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка импорта", e);
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }


}
