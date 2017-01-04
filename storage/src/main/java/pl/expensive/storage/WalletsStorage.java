package pl.expensive.storage;

import java.util.Collection;

public interface WalletsStorage {
    /**
     * List all stored {@link Wallet}s. Result is null safe
     *
     * @return Collection of all stored Wallets or Collections.emptyList() for empty result.
     */
    Collection<Wallet> list();

    /**
     * Stores {@link Wallet} object.
     *
     * @param wallet There is no validation of object performed. Check for nulls or invalid values before using.
     * @throws IllegalStateException In case of storing duplicate.
     */
    void insert(Wallet wallet) throws IllegalStateException;
}
