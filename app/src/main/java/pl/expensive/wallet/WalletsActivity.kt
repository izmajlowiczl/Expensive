package pl.expensive.wallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_wallets.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.WalletsStorage
import pl.expensive.transaction.TransactionsAdapter

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

        val vRecycler = findViewById(R.id.transactions) as RecyclerView
        vRecycler.layoutManager = LinearLayoutManager(this)
        vRecycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        showWallets()
    }

    private fun showWallets() {
        wallets.update(ViewState.Loading())
        loading.visibility = VISIBLE

        doAsync {
            val data = walletStorage.list()
            val transactionData = transactionStorage.select()

            val viewModels = data.map {
                val uuid = it.uuid
                WalletViewModel(it.name, transactionData.filter { it.wallet == uuid }, it.currency)
            }

            uiThread {
                adapter.data = transactionData
                wallets.update(if (viewModels.isNotEmpty()) ViewState.Wallets(viewModels) else ViewState.Empty())
                loading.visibility = GONE
                wallets.visibility = VISIBLE
                transactions.visibility = VISIBLE
            }
        }
    }
}
