package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage._Seeds.CASH
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
        val beer = Transaction.create(UUID.randomUUID(), CASH.uuid, BigDecimal("4.99"), CASH.currency, Date().time, "Beer")

        storage.insert(beer)

        assertThat(storage.select(CASH.uuid))
                .containsExactly(beer)
    }

    @Test
    fun selectTransaction() {
        val beer = Transaction.create(UUID.randomUUID(), CASH.uuid, BigDecimal("4.99"), CASH.currency, Date().time, "Beer")
        storage.insert(beer)

        assertThat(storage.select(CASH.uuid))
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
        val beer = Transaction.create(UUID.randomUUID(), CASH.uuid, BigDecimal("4.99"), Currency("NOT_EXISTING_CURRENCY", "#.###"), Date().time, "Beer")

        storage.insert(beer)
    }
}
