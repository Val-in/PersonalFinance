package storage;

import model.User;
import storage.UserStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileUserStorage implements UserStorage {

    private static final String FILE = "data/users.dat";

    @Override
    public void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения пользователей", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, User> loadUsers() {
        File file = new File(FILE);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки пользователей", e);
        }
    }
}
