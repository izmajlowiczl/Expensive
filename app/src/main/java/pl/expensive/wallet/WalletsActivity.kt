package pl.expensive.wallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_wallets.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.WalletsStorage
import pl.expensive.transaction.TransactionsAdapter

class WalletsActivity : AppCompatActivity(), WalletsViewContract {
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

        val vRecycler = findViewById(R.id.transactions) as RecyclerView
        vRecycler.layoutManager = LinearLayoutManager(this)
        vRecycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        showWallets()
    }

    private fun showWallets() {
        update(ViewState.Loading())

        doAsync(exceptionHandler = { update(ViewState.Error(getString(R.string.err_loading_wallets))) }) {
            val data = walletStorage.list().first()
            val transactionData = transactionStorage.select()
            val viewModel = WalletViewModel(
                    data.name,
                    transactionData.filter { it.wallet == data.uuid },
                    data.currency)

            uiThread {
                update(ViewState.Wallets(viewModel))
            }
        }
    }

    override fun update(viewState: ViewState) {
        when (viewState) {
            is ViewState.Loading -> {
                wallets.visibility = GONE
                transactions.visibility = GONE
                loading.visibility = VISIBLE
            }
            is ViewState.Wallets -> {
                wallets.visibility = VISIBLE
                transactions.visibility = VISIBLE
                loading.visibility = GONE

                adapter.data = viewState.viewModels.transactions
                wallets.update(viewState.viewModels)
            }
            is ViewState.Error -> {
                wallets.visibility = GONE
                transactions.visibility = GONE
                loading.visibility = GONE

                // TODO: show error view here instead toast
                Toast.makeText(this, viewState.err, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
