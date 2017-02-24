package pl.expensive;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;
import pl.expensive.wallet.TransactionsModel;
import pl.expensive.wallet.WalletsService;

@Module
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    TransactionsModel transactionsModel(Resources resources,
                                        WalletsService walletsService,
                                        TransactionStorage transactionStorage) {
        return new TransactionsModel(walletsService, transactionStorage, resources);
    }

    @Provides
    @Singleton
    Resources resources() {
        return context.getResources();
    }

    @Provides
    @Singleton
    WalletsService walletsService(WalletsStorage walletsStorage) {
        return new WalletsService(walletsStorage);
    }
}
