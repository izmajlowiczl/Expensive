package pl.expensive.storage

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored

@RunWith(AndroidJUnit4::class)
class CurrencyStorageTest {

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
