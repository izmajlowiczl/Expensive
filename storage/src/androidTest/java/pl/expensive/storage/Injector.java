package pl.expensive.storage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

public class Injector {
    public static Context provideContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    public static Database provideDatabase() {
        return new Database(provideContext(), null);
    }
}
