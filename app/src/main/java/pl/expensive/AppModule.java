package pl.expensive;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;
import pl.expensive.wallet.DisplayWallets;

@Module
public class AppModule {
    @Provides
    DisplayWallets displayWallets(WalletsStorage walletStorage, TransactionStorage transactionStorage) {
        return new DisplayWallets(walletStorage, transactionStorage);
    }
}
