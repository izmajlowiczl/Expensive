package pl.expensive.storage;

import android.support.annotation.NonNull;

import java.util.Collection;

public interface WalletsStorage {
    @NonNull
    Collection<Wallet> list();

    void insert(@NonNull Wallet wallet);
}
