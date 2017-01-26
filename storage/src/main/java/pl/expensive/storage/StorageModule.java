package pl.expensive.storage;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
    private final Context context;

    public StorageModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    Database database() {
        return new Database(context);
    }

    @Singleton
    @Provides
    WalletsStorage walletModel(Database database) {
        return new SQLiteBasedWalletsStorage(database);
    }

    @Singleton
    @Provides
    TransactionStorage transactionStorage(Database database) {
        return new SQLiteBasedTransactionStorage(database);
    }
}
