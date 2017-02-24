package pl.expensive

import android.content.Intent
import pl.expensive.storage.Transaction
import pl.expensive.transaction.NewTransactionActivity
import pl.expensive.wallet.TransactionsActivity

fun TransactionsActivity.startEditTransactionScreen(touchPos: IntArray, transaction: Transaction) {
    val intent = Intent(this, NewTransactionActivity::class.java)
            .putExtra("transaction_uuid", transaction.uuid.toString())
            .putExtra("transaction_amount", transaction.amount.abs().toString())
            .putExtra("transaction_desc", transaction.description)
            .putExtra("loc", touchPos)
    startActivityForResult(intent, 666)
}

