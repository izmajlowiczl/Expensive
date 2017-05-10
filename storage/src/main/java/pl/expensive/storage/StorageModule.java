package pl.expensive.storage;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
    private final Context context;

    public StorageModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    SharedPreferences preferences() {
        return context.getSharedPreferences("expensive_prefs", 0);
    }

    @Singleton
    @Provides
    Database database(SharedPreferences prefs) {
        return new Database(context, prefs);
    }

    @Singleton
    @Provides
    TransactionStorage transactionStorage(Database database) {
        return new SQLiteBasedTransactionStorage(database);
    }
}
