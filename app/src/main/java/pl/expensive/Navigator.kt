package pl.expensive

import android.content.Intent
import pl.expensive.storage.Transaction
import pl.expensive.transaction.details.NewTransactionActivity
import pl.expensive.transaction.list.TransactionsActivity

fun TransactionsActivity.startNewTransactionCreatorScreen() {
    val intent = Intent(this, NewTransactionActivity::class.java)
    startActivityForResult(intent, 666)
    overridePendingTransition(0, 0)
}

fun TransactionsActivity.startEditTransactionScreen(transaction: Transaction) {
    val intent = Intent(this, NewTransactionActivity::class.java)
            .putExtra("transaction_uuid", transaction.uuid.toString())
            .putExtra("transaction_amount", transaction.amount.abs().toString())
            .putExtra("transaction_desc", transaction.description)
    startActivityForResult(intent, 666)
}

