package pl.expensive;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.WalletsStorage;
import pl.expensive.wallet.WalletsService;

@Module
public class AppModule {
    @Provides
    @Singleton
    WalletsService walletsService(WalletsStorage walletsStorage) {
        return new WalletsService(walletsStorage);
    }
}
