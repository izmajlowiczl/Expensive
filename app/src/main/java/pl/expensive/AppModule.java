package pl.expensive;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.WalletsStorage;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    FetchWallets fetchWallets(WalletsStorage storage) {
        return new FetchWallets(storage);
    }
}
