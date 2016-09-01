package pl.expensive;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.expensive.db.Database;
import pl.expensive.db.WalletModel;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Database database() {
        return new Database(application.getApplicationContext());
    }

    @Singleton
    @Provides
    WalletModel walletModel(Database database) {
        return new WalletModel(database);
    }

    @Provides
    DisplayWallets displayWallets(WalletModel walletModel) {
        return new DisplayWallets(walletModel);
    }
}
