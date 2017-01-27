package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored

@RunWith(AndroidJUnit4::class)
class CurrencyStorageTest {
    private lateinit var storage: CurrencyStorage
    private lateinit var db: SQLiteDatabase

    @Before
    fun setUp() {
        val inMemDatabase = Injector.provideDatabase()
        db = inMemDatabase.writableDatabase
        storage = SQLiteBasedCurrencyStorage(inMemDatabase)
    }

    @Test
    fun storeSingleCurrency() {
        val currency = Currency("FAKE", "$#.##")

        storage.insert(currency)

        assertCurrencyCodeStored(db, "FAKE")
    }

    @Test(expected = IllegalStateException::class)
    fun storeDuplicates() {
        val currency = Currency("FAKE", "$#.##")

        storage.insert(currency)
        storage.insert(currency)
    }
}
