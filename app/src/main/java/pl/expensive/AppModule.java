package pl.expensive;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.transaction.list.TransactionsModel;

@Module
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    TransactionsModel transactionsModel(Resources resources,
                                        TransactionStorage transactionStorage) {
        return new TransactionsModel(transactionStorage, resources);
    }

    @Provides
    @Singleton
    Resources resources() {
        return context.getResources();
    }
}
