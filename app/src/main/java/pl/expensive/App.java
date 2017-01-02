package pl.expensive;

import android.app.Application;

import pl.expensive.storage.StorageModule;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppComponent appComponent = DaggerAppComponent.builder()
                .storageModule(new StorageModule(this))
                .appModule(new AppModule(this))
                .build();
        Injector.initAppComponent(appComponent);
    }
}
