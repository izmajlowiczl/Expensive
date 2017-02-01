package pl.expensive.transaction

import org.threeten.bp.LocalDateTime
import pl.expensive.storage.Transaction

object TransactionGrouper {
    fun group(sortedTransactions: List<Transaction>): Map<LocalDateTime, List<Transaction>> {
        if (sortedTransactions.isEmpty()) {
            return emptyMap()
        }


        var prev = sortedTransactions[0].toLocalDateTime()
        val result = mutableMapOf(prev to mutableListOf(sortedTransactions[0]))

        for (i in 1..sortedTransactions.size - 1) {
            val transaction = sortedTransactions[i]
            val currentTransactionTime = transaction.toLocalDateTime()

            if (currentTransactionTime.toLocalDate().isEqual(prev.toLocalDate())) {
                result[currentTransactionTime]!!.add(transaction)
            } else {
                result.put(currentTransactionTime, mutableListOf(transaction))
            }

            prev = currentTransactionTime
        }

        return result
    }
}
