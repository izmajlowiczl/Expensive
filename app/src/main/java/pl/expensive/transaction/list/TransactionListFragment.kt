package pl.expensive.transaction.list

import android.content.Context
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
import pl.expensive.storage.Transaction

class TransactionListFragment : Fragment() {
    interface TransactionListCallbacks {
        fun onTransactionSelected(transaction: Transaction)
        fun onMonthSelected(month: YearMonth)
    }

    private val adapter by lazy {
        val transactionClickFun: (Transaction) -> Unit = { transition -> callback?.onTransactionSelected(transition) }
        val headerClickFun: (YearMonth) -> Unit = { callback?.onMonthSelected(it) }
        TransactionsAdapter(transactionClickFun, headerClickFun)
    }
    private var callback: TransactionListCallbacks? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_transactions_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vTransactions.layoutManager = LinearLayoutManager(activity)
        vTransactions.adapter = adapter
        vTransactions.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        if (activity is TransactionListCallbacks) {
            callback = activity
        } else {
            throw RuntimeException(activity!!.toString() + " must implement TransactionListCallbacks")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

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

