package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored
import pl.expensive.storage.DatabaseSchemaTestHelper.assertLabelStored
import java.util.*

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
    fun containsFoodLabel() {
        assertLabelStored(db, Label(UUID.fromString("78167ed1-a59a-431e-bfd4-57fb8dbed743"), "Food"))
    }

    @Test
    fun containsRentLabel() {
        assertLabelStored(db, Label(UUID.fromString("edb24ade-2ac1-414e-9281-4d7a3b847d1b"), "Rent"))
    }

    @Test
    fun containsHouseholdLabel() {
        assertLabelStored(db, Label(UUID.fromString("e4655b6b-3e75-4ca7-86b5-25d7e4906ada"), "Household"))
    }

    @Test
    fun containsTransportationLabel() {
        assertLabelStored(db, Label(UUID.fromString("7c108b80-c954-40bb-8464-0914d28426d5"), "Transportation"))
    }
}


