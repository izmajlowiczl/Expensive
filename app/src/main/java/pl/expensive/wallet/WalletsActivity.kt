package pl.expensive.wallet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_wallets.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.WalletsStorage
import pl.expensive.transaction.*
import java.util.*


class WalletsActivity : AppCompatActivity() {
    private val walletStorage: WalletsStorage by lazy(mode = LazyThreadSafetyMode.NONE) {
        Injector.app().wallets()
    }
    private val transactionStorage: TransactionStorage by lazy(mode = LazyThreadSafetyMode.NONE) {
        Injector.app().transactions()
    }

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        TransactionsAdapter(newTransactionClickListener = {
            startActivity(Intent(this@WalletsActivity, NewTransactionActivity::class.java))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)
        Injector.app().inject(this)

        setSupportActionBar(toolbar)

        val vRecycler = findViewById(R.id.transactions) as RecyclerView
        vRecycler.layoutManager = LinearLayoutManager(this)
        vRecycler.adapter = adapter
        vRecycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
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

    private fun update(viewState: ViewState) = when (viewState) {
        is ViewState.Loading -> {
            transactions.visibility = GONE
            loading.visibility = VISIBLE
        }
        is ViewState.Wallets -> {
            transactions.visibility = VISIBLE
            loading.visibility = GONE

            val result = mutableListOf<Any>()
            val today = LocalDateTime.now()
            TransactionGrouper.group(viewState.viewModels.transactions.filter {
                !it.toLocalDateTime().isAfter(today)
            }).forEach {
                result.add(Header(it.formatHeader()))
                result.addAll(it.value)
            }
            result.add(if (result.isNotEmpty()) 1 else 0, NewTransactionPlaceHolder())

            adapter.data = result
            supportActionBar!!.title = viewState.viewModels.formattedTitle()
        }
        is ViewState.Error -> {
            transactions.visibility = GONE
            loading.visibility = GONE

            // TODO: show error view here instead toast
            Toast.makeText(this, viewState.err, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Creates Spannable with month name at full size,
     * followed by short year  (eg '17 from 2017) with 60% text size (from month name)
     */
    private fun Map.Entry<YearMonth, List<Transaction>>.formatHeader(): Spannable {
        val month = key.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize()
        val year = if (key.year > 2000) key.year - 2000 else key.year

        val span = SpannableString("$month '$year")
        val start = month.length
        val end = span.length
        span.setSpan(SuperscriptSpan(), start, end, 0)
        span.setSpan(RelativeSizeSpan(.6f), start, end, 0)

        return span
    }

    /**
     * Crates Spannable with wallet's name at 60% text size (from amount),
     * followed by total amount at full text size.
     */
    private fun WalletViewModel.formattedTitle(): Spannable {
        val span: Spannable = SpannableString("$name  ${formattedTotal()}")
        span.setSpan(RelativeSizeSpan(.6f), 0, name.length, 0)
        return span
    }
}

