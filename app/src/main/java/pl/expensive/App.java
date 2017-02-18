package pl.expensive;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import pl.expensive.storage.StorageModule;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AndroidThreeTen.init(this);

        if (BuildConfig.DEBUG && BuildConfig.LEAK_CANARY_ENABLED) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }

        AppComponent appComponent = DaggerAppComponent.builder()
                .storageModule(new StorageModule(this))
                .build();
        Injector.initAppComponent(appComponent);
    }
}
