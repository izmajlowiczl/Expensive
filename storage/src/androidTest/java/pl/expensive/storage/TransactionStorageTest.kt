package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage._Seeds.CASH
import pl.expensive.storage._Seeds.CASH_ID
import pl.expensive.storage._Seeds.EUR
import java.math.BigDecimal
import java.util.*

@RunWith(AndroidJUnit4::class)
class TransactionStorageTest {
    lateinit var db: SQLiteDatabase
    lateinit var storage: TransactionStorage

    @Before
    fun setUp() {
        val inMemDatabase = Injector.provideDatabase()
        db = inMemDatabase.writableDatabase
        storage = SQLiteBasedTransactionStorage(inMemDatabase)
    }

    @Test
    fun storeSingleTransaction() {
        val beer = Transaction.create(UUID.randomUUID(), CASH_ID, BigDecimal("4.99"), CASH.currency, Date().time, "Beer")

        storage.insert(beer)

        assertThat(storage.select(CASH_ID))
                .containsExactly(beer)
    }

    @Test
    fun selectTransaction() {
        val beer = Transaction.create(UUID.randomUUID(), CASH_ID, BigDecimal("4.99"), CASH.currency, Date().time, "Beer")
        storage.insert(beer)

        assertThat(storage.select(CASH_ID))
                .containsExactly(beer)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun listingAllTransactionsIsNotSupported() {
        storage.list()
    }

    @Test(expected = IllegalStateException::class)
    fun withoutStoredWallet() {
        val beer = Transaction.create(UUID.randomUUID(), UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer")

        storage.insert(beer)
    }

    @Test(expected = IllegalStateException::class)
    fun withoutStoredCurrency() {
        val beer = Transaction.create(UUID.randomUUID(), CASH_ID, BigDecimal("4.99"), Currency("NOT_EXISTING_CURRENCY", "#.###"), Date().time, "Beer")

        storage.insert(beer)
    }

    fun withCategory() {
        val cat = Category("Food")
        val beer = Transaction(UUID.randomUUID(), CASH_ID, BigDecimal("4.99"), EUR, Date().time, "Beer", cat)
        storage.insert(beer)

        val transactions = storage.select(CASH_ID)
        assertThat(transactions).containsExactly(beer)
        assertThat(transactions.first().category).isEqualTo(cat)
    }

    fun categoryIsOptional() {
        val cat = null
        val beer = Transaction(UUID.randomUUID(), CASH_ID, BigDecimal("4.99"), EUR, Date().time, "Beer", cat)
        storage.insert(beer)

        val transactions = storage.select(CASH_ID)
        assertThat(transactions).containsExactly(beer)
        assertThat(transactions.first().category).isNull()
    }
}
