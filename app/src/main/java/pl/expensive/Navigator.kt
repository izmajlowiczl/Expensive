package pl.expensive

import android.content.Intent
import org.threeten.bp.YearMonth
import pl.expensive.month_overview.MonthOverviewActivity
import pl.expensive.storage.Transaction
import pl.expensive.transaction.details.TransactionDetailsActivity
import pl.expensive.transaction.list.TransactionsActivity

fun TransactionsActivity.startNewTransactionCreatorScreen() {
    val intent = Intent(this, TransactionDetailsActivity::class.java)
    startActivityForResult(intent, 666)
    overridePendingTransition(0, 0)
}

fun TransactionsActivity.startEditTransactionScreen(transaction: Transaction) {
    val intent = Intent(this, TransactionDetailsActivity::class.java)
            .putExtra("transaction_uuid", transaction.uuid.toString())
            .putExtra("transaction_amount", transaction.amount.abs().toString())
            .putExtra("transaction_desc", transaction.description)
    startActivityForResult(intent, 666)
}

const val extra_month_overview_date = "extra_month_overview_date"
fun TransactionsActivity.startMonthOverviewScreen(date: YearMonth) {
    val intent = Intent(this, MonthOverviewActivity::class.java)
    intent.putExtra(extra_month_overview_date, date)
    startActivity(intent)
}
