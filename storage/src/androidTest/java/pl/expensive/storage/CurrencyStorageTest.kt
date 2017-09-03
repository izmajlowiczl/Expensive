package pl.expensive.storage

import android.content.SharedPreferences
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored

@RunWith(AndroidJUnit4::class)
class CurrencyStorageTest {

    @Test
    fun fallbackCurrencyWhenAccessingNotStoredDefaultCurrency() {
        val prefs = Injector.provideKeyValueStorage()
        clearPrefs(prefs)

        val default = getDefaultCurrency(prefs, _Seeds.CZK)

        assertThat(default).isEqualTo(_Seeds.CZK)
    }

    @Test
    fun storedDefaultCurrencyInsteadOfFallback() {
        val prefs = Injector.provideKeyValueStorage()
        clearPrefs(prefs)

        val currency = Currency("FAKE", "$#.##")
        setDefaultCurrency(currency, prefs)

        val default = getDefaultCurrency(prefs, _Seeds.CZK)

        assertThat(default).isEqualTo(currency)
    }

    private fun clearPrefs(prefs: SharedPreferences) {
        prefs.edit().clear().apply()
    }

    @Test
    fun storeSingleCurrency() {
        val currency = Currency("FAKE", "$#.##")

        val database = Injector.provideDatabase()
        insertCurrency(currency, database)

        assertCurrencyCodeStored(database.readableDatabase, "FAKE")
    }

    @Test(expected = IllegalStateException::class)
    fun storeDuplicates() {
        val currency = Currency("FAKE", "$#.##")

        val database = Injector.provideDatabase()
        insertCurrency(currency, database)
        insertCurrency(currency, database)
    }
}
