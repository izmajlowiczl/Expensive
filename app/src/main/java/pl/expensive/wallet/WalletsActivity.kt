package pl.expensive.wallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_wallets.*
import org.jetbrains.anko.toast
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.formatValue
import pl.expensive.hideKeyboard
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.WalletsStorage
import pl.expensive.storage._Seeds
import pl.expensive.transaction.TransactionsAdapter
import java.math.BigDecimal



class WalletsActivity : AppCompatActivity() {
    private val walletStorage: WalletsStorage by lazy(mode = LazyThreadSafetyMode.NONE) {
        Injector.app().wallets()
    }
    private val transactionStorage: TransactionStorage by lazy(mode = LazyThreadSafetyMode.NONE) {
        Injector.app().transactions()
    }
    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        TransactionsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)
        Injector.app().inject(this)

        setSupportActionBar(toolbar)

        val vRecycler = findViewById(R.id.transactions) as RecyclerView
        vRecycler.layoutManager = LinearLayoutManager(this)
        vRecycler.adapter = adapter

        vCreateTransaction.setOnClickListener {
            val amountText = vCreateTransactionAmount.text.toString()
            if (amountText.isNullOrEmpty()) {
                vCreateTransactionAmount.error = "Mandatory"
            } else {
                vCreateTransactionAmount.error = null

                update(ViewState.Loading())

                val amount = BigDecimal(amountText)
                transactionStorage.insert(Transaction.withAmount(amount = amount))
                vCreateTransactionAmount.text.clear()
                vCreateTransactionAmount.hideKeyboard()

                toast("Transaction for ${_Seeds.EUR.formatValue(money = amount)} created!")

                showWallets()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        update(ViewState.Loading())
        showWallets()
    }

    private fun showWallets() {
        val data = walletStorage.list().first()
        val transactionData = transactionStorage.select().sortedByDescending { it.date }
        val viewModel = WalletViewModel(
                data.name,
                transactionData.filter { it.wallet == data.uuid },
                data.currency)

        update(ViewState.Wallets(viewModel))
    }

    private fun update(viewState: ViewState) {
        when (viewState) {
            is ViewState.Loading -> {
                transactions.visibility = GONE
                loading.visibility = VISIBLE
            }
            is ViewState.Wallets -> {
                transactions.visibility = VISIBLE
                loading.visibility = GONE

                adapter.data = viewState.viewModels.transactions

                toolbar.title = "${viewState.viewModels.name}  ${viewState.viewModels.formattedTotal()}"
            }
            is ViewState.Error -> {
                transactions.visibility = GONE
                loading.visibility = GONE

                // TODO: show error view here instead toast
                Toast.makeText(this, viewState.err, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
