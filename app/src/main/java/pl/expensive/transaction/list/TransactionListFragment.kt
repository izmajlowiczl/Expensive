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
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.show
import pl.expensive.storage.Transaction
import java.math.BigDecimal

class TransactionListFragment : Fragment() {
    interface TransactionListCallbacks {
        fun onTransactionSelected(transaction: Transaction)
    }

    private val adapter by lazy { TransactionsAdapter(transactionClickFun = { transition -> callback?.onTransactionSelected(transition) }) }
    private val transactionsModel by lazy { Injector.app().transactionsModel() }

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

    override fun onResume() {
        super.onResume()
        transactionsModel.showWallets(update)
    }

    private val update: (ViewState) -> Unit = {
        when (it) {
            is ViewState.Wallets -> {
                vTransactions.show(true)
                adapter.data = it.adapterData
                vTransactionsEmptyMsg.show(false)

            }

            is ViewState.Empty -> {
                vTransactions.show(false)
                vTransactionsEmptyMsg.show()
            }
        }
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

    fun onNewItem(amount: BigDecimal) {
        transactionsModel.quickAdd(amount, update)
    }
}

