package pl.expensive.transaction.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_transactions_list.*
import org.threeten.bp.YearMonth
import pl.expensive.R
import pl.expensive.show
import pl.expensive.storage.TransactionDbo

/**
 *
 * Displays list of transactions after calling  showTransactions(data) function
 * or empty placeholder after calling showEmpty() function.
 *
 * Exposes an listener for row item and month header click events (TransactionListCallbacks interface).
 * Use attachCallbacks/detachCallbacks when you want to listen for click events.
 * When maybeCallback is not attached, click events won't be populated.
 *
 */
class TransactionListFragment : Fragment() {
    interface TransactionListCallbacks {
        fun onTransactionSelected(transaction: TransactionDbo)
        fun onMonthSelected(month: YearMonth)
    }

    private var maybeCallback: TransactionListCallbacks? = null

    private val adapter by lazy {
        val transactionClickFun: (TransactionDbo) -> Unit = { transition -> maybeCallback?.onTransactionSelected(transition) }
        val headerClickFun: (YearMonth) -> Unit = { maybeCallback?.onMonthSelected(it) }
        TransactionsAdapter(transactionClickFun, headerClickFun)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_transactions_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vTransactions.layoutManager = LinearLayoutManager(activity)
        vTransactions.adapter = adapter
        vTransactions.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    fun attachCallbacks(callbacks: TransactionListCallbacks) {
        this.maybeCallback = callbacks
    }

    fun detachCallbacks() {
        maybeCallback = null
    }

    // TODO: data: List<Any> is not the best idea.. encapsulate it to more meaningful data class
    fun showTransactions(data: List<Any>) {
        vTransactionsEmptyMsg.show(false)
        vTransactions.show(true)
        adapter.replaceAll(data)
    }

    fun showEmpty() {
        vTransactions.show(false)
        vTransactionsEmptyMsg.show()
    }
}
