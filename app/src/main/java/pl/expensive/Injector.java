package pl.expensive;

public class Injector {
    private static AppComponent appComponent;

    private Injector() {
        // Do not access
    }

    public static void initAppComponent(AppComponent appComponent) {
        Injector.appComponent = appComponent;
    }

    public static AppComponent app() {
        return appComponent;
    }
}
