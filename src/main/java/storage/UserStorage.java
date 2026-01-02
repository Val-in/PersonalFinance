package storage;

import model.User;
import java.util.Map;

public interface UserStorage {
    void saveUsers(Map<String, User> users);
    Map<String, User> loadUsers();
}
