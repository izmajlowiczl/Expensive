package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.OTHER
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
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer", OTHER)

        storage.insert(beer)

        assertThat(storage.list())
                .containsExactly(beer)
    }

    @Test
    fun listTransactions() {
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer", OTHER)
        storage.insert(beer)

        assertThat(storage.list())
                .containsExactly(beer)
    }

    @Test(expected = IllegalStateException::class)
    fun withoutStoredCurrency() {
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer", OTHER)

        storage.insert(beer)
    }

    fun withCategory() {
        val cat = Category(name = "Food")
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer", cat)
        storage.insert(beer)

        val transactions = storage.list()
        assertThat(transactions).containsExactly(beer)
        assertThat(transactions.first().category).isEqualTo(cat)
    }

    fun categoryIsOptional() {
        val cat = null
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer", cat)
        storage.insert(beer)

        val transactions = storage.list()
        assertThat(transactions).containsExactly(beer)
        assertThat(transactions.first().category).isNull()
    }
}
