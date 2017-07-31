package pl.expensive.storage

import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage._Seeds.EUR
import java.math.BigDecimal
import java.util.*

@RunWith(AndroidJUnit4::class)
class TransactionStorageTest {

    private val database = Injector.provideDatabase()
    @Test
    fun storeSingleTransaction() {
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer")

        insertTransaction(beer, database)

        assertThat(listTransactions(database))
                .containsExactly(beer)
    }

    @Test
    fun listTransactions() {
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), EUR, Date().time, "Beer")
        insertTransaction(beer, database)

        assertThat(listTransactions(database))
                .containsExactly(beer)
    }

    @Test(expected = IllegalStateException::class)
    fun withoutStoredCurrency() {
        val beer = Transaction(UUID.randomUUID(), BigDecimal("4.99"), Currency("FAKE", "##-$$"), Date().time, "Beer")

        insertTransaction(beer, database)
    }
}
