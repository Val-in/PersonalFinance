package storage;

import java.io.*;
import model.Wallet;

public class FileWalletStorage implements WalletStorage {

  private static final String DIR = "data";

  public FileWalletStorage() {
    new File(DIR).mkdirs();
  }

  @Override
  public void save(String username, Wallet wallet) {
    File file = new File(DIR + "/" + username + ".dat");

    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
      out.writeObject(wallet);
    } catch (IOException e) {
      throw new RuntimeException("Ошибка сохранения кошелька", e);
    }
  }

  @Override
  public Wallet load(String username) {
    File file = new File(DIR + "/" + username + ".dat");

    if (!file.exists()) {
      return new Wallet();
    }

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
      return (Wallet) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Ошибка загрузки кошелька", e);
    }
  }
}
