package pl.expensive.transaction

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.threeten.bp.LocalDateTime
import pl.expensive.storage.Transaction
import pl.expensive.storage._Seeds.EUR
import java.math.BigDecimal
import java.util.*

class TransactionGroupperTest {

    @Test
    fun empty() {
        assertThat(TransactionGrouper.group(ArrayList<Transaction>())).isEmpty()
    }

    @Test
    fun singleTransaction() {
        val transactions = Arrays.asList(transactionAt(SOME_DAY))

        assertThat(TransactionGrouper.group(transactions))
                .containsExactly(SOME_DAY, transactions)
    }

    @Test
    fun transactionForTheSameDay() {
        val transactions = Arrays.asList(
                transactionAt(SOME_DAY),
                transactionAt(SOME_DAY))

        assertThat(TransactionGrouper.group(transactions))
                .containsExactly(SOME_DAY, transactions)
    }

    @Test
    fun transactionsForDifferentDays() {
        val t1 = transactionAt(SOME_DAY)
        val t2 = transactionAt(SOME_DAY.plusDays(1))
        val transactions = Arrays.asList(t1, t2)

        val group = TransactionGrouper.group(transactions)
        assertThat(group.size).isEqualTo(2)
        assertThat(group[SOME_DAY]).containsExactly(t1)
        assertThat(group[SOME_DAY.plusDays(1)]).containsExactly(t2)
    }

    private fun transactionAt(today: LocalDateTime): Transaction {
        return Transaction.create(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, EUR, today, "")
    }

    companion object {
        private val SOME_DAY = LocalDateTime.of(2001, 10, 19, 10, 20, 0)
    }
}
