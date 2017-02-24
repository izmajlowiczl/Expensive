package pl.expensive.storage

import java.util.*

interface TransactionStorage : Storage<Transaction> {
    fun select(wallet: UUID = _Seeds.CASH_ID): List<Transaction>
    fun update(transaction: Transaction)
}
