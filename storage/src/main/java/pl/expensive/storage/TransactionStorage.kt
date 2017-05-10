package pl.expensive.storage

interface TransactionStorage : Storage<Transaction> {
    fun update(transaction: Transaction)
}
