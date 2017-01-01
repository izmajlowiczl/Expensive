package pl.expensive;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.Database;
import pl.expensive.storage.SQLiteBasedWalletsStorage;
import pl.expensive.storage.WalletsStorage;

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
    WalletsStorage walletModel(Database database) {
        return new SQLiteBasedWalletsStorage(database);
    }
}
