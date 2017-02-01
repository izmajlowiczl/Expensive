package pl.expensive;

import javax.inject.Singleton;

import dagger.Component;
import pl.expensive.storage.StorageModule;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;

@Singleton
@Component(modules = {StorageModule.class, AppModule.class})
public interface AppComponent {
    void inject(WalletsActivity walletsActivity);

    DisplayWallets displayWallets();

    WalletsStorage wallets();

    TransactionStorage transactions();
}
