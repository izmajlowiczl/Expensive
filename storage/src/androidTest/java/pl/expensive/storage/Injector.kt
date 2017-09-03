package pl.expensive.storage

import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry

internal object Injector {
    fun provideContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    fun provideDatabase(): Database {
        return Database(provideContext(), provideKeyValueStorage(), null)
    }

    fun provideKeyValueStorage(): SharedPreferences = provideContext().getSharedPreferences("test_sp", 0)
}
