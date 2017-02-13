package pl.expensive.transaction

import org.threeten.bp.YearMonth
import pl.expensive.storage.Transaction

object TransactionGrouper {
    fun group(sortedTransactions: List<Transaction>): Map<YearMonth, List<Transaction>> {
        return sortedTransactions.groupBy { YearMonth.of(it.toLocalDateTime().year, it.toLocalDateTime().month)}
    }
}
