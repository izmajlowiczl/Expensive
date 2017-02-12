package pl.expensive.transaction

import org.threeten.bp.LocalDate
import pl.expensive.storage.Transaction

object TransactionGrouper {
    fun group(sortedTransactions: List<Transaction>): Map<LocalDate, List<Transaction>> {
        return sortedTransactions.groupBy { it.toLocalDateTime().toLocalDate() }
    }
}
