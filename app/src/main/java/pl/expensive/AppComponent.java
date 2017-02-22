package pl.expensive;

import javax.inject.Singleton;

import dagger.Component;
import pl.expensive.storage.StorageModule;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;
import pl.expensive.transaction.NewTransactionActivity;
import pl.expensive.wallet.WalletsActivity;
import pl.expensive.wallet.WalletsService;

@Singleton
@Component(modules = {StorageModule.class, AppModule.class})
public interface AppComponent {
    void inject(WalletsActivity walletsActivity);
    void inject(NewTransactionActivity newTransactionActivity);

    WalletsService walletsService();
    WalletsStorage wallets();

    TransactionStorage transactions();
}
