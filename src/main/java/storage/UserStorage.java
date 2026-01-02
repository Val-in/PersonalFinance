package storage;

import java.util.Map;
import model.User;

public interface UserStorage {
  void saveUsers(Map<String, User> users);

  Map<String, User> loadUsers();
}
