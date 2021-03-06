package pl.expensive.transaction.list

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneOffset
import pl.expensive.storage.TransactionDbo
import pl.expensive.storage._Seeds.EUR
import java.math.BigDecimal
import java.util.*

class TransactionGroupperTest {

    @Test
    fun empty() {
        assertThat(group(ArrayList<TransactionDbo>())).isEmpty()
    }

    @Test
    fun singleTransaction() {
        val transactions = Arrays.asList(transactionAt(SOME_DAY))

        assertThat(group(transactions))
                .containsExactly(SOME_DAY_YEAR_MONTH, transactions)
    }

    @Test
    fun transactionForTheSameDay() {
        val transactions = listOf(transactionAt(SOME_DAY), transactionAt(SOME_DAY))

        val group = group(transactions)

        assertThat(group)
                .containsExactly(SOME_DAY_YEAR_MONTH, transactions)
    }

    @Test
    fun transactionsForDifferentDays() {
        val t1 = transactionAt(SOME_DAY)
        val t2 = transactionAt(SOME_DAY.plusDays(1))
        val transactions = listOf(t1, t2)

        val group = group(transactions)

        assertThat(group[SOME_DAY_YEAR_MONTH]).containsExactly(t1, t2)
    }

    @Test
    fun multipleTransactionsForDifferentDays() {
        val d1t1 = transactionAt(SOME_DAY)
        val d1t2 = transactionAt(SOME_DAY)
        val d2t1 = transactionAt(SOME_DAY.plusMonths(1))
        val d2t2 = transactionAt(SOME_DAY.plusMonths(1))
        val transactions = listOf(d1t1, d1t2, d2t1, d2t2)

        val group = group(transactions)
        assertThat(group.size).isEqualTo(2)
        assertThat(group[SOME_DAY_YEAR_MONTH]).containsExactly(d1t1, d1t2)
        assertThat(group[SOME_DAY_YEAR_MONTH.plusMonths(1)]).containsExactly(d2t1, d2t2)
    }

    private fun transactionAt(today: LocalDateTime): TransactionDbo {
        return TransactionDbo(UUID.randomUUID(), BigDecimal.TEN, EUR, today.toMillisUTC(), "")
    }

    private fun LocalDateTime.toMillisUTC(): Long =
            toInstant(ZoneOffset.UTC).toEpochMilli()

    companion object {
        private val SOME_DAY = LocalDateTime.of(2001, 10, 19, 10, 20, 0)
        private val SOME_DAY_YEAR_MONTH = YearMonth.of(2001, 10)
    }
}
