package pl.expensive;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;

@Module
public class AppModule {
    @Provides
    DisplayWallets displayWallets(WalletsStorage walletStorage, TransactionStorage transactionStorage) {
        return new DisplayWallets(walletStorage, transactionStorage);
    }
}
