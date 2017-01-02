package pl.expensive;

import javax.inject.Singleton;

import dagger.Component;
import pl.expensive.storage.StorageModule;

@Singleton
@Component(modules = {AppModule.class, StorageModule.class})
public interface AppComponent {
    void inject(WalletsActivity walletsActivity);
}
