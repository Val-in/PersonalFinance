package storage;

import model.Wallet;

public interface WalletStorage {
    void save(String username, Wallet wallet);
    Wallet load(String username);
}