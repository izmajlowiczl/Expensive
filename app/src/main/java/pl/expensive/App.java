package pl.expensive;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        Injector.initAppComponent(appComponent);
    }
}
