package pl.expensive.storage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

class Injector {
    static Context provideContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    static Database provideDatabase() {
        return new Database(provideContext(), null);
    }
}
