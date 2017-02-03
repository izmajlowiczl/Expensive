package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage.DatabaseSchemaTestHelper.assertCategoryNameStored
import pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored

@RunWith(AndroidJUnit4::class)
class DatabaseSeedsTest {
    internal lateinit var database: Database
    internal lateinit var db: SQLiteDatabase

    @Before
    fun setup() {
        database = Injector.provideDatabase()
        db = database.readableDatabase
    }

    @Test
    fun containsPrePopulatedCashWallet() {
        val cursor = db.rawQuery("SELECT name FROM tbl_wallet", null)
        if (cursor == null || !cursor.moveToNext()) {
            fail("Cannot find pre populated cash row")
        }

        assertThat(cursor!!.getString(0))
                .isEqualTo("Cash")
    }

    @Test
    fun containsEurCurrency() {
        assertCurrencyCodeStored(db, "EUR")
    }

    @Test
    fun containsGBPCurrency() {
        assertCurrencyCodeStored(db, "GBP")
    }

    @Test
    fun containsCHFCurrency() {
        assertCurrencyCodeStored(db, "CHF")
    }

    @Test
    fun containsPLNCurrency() {
        assertCurrencyCodeStored(db, "PLN")
    }

    @Test
    fun containsCZKCurrency() {
        assertCurrencyCodeStored(db, "CZK")
    }

    @Test
    fun containsFoodCategory() {
        assertCategoryNameStored(db, "Food")
    }

    @Test
    fun containsOtherCategory() {
        assertCategoryNameStored(db, "Other")
    }
}
