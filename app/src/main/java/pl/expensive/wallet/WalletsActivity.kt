package pl.expensive.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_wallets.*
import org.jetbrains.anko.toast
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.calculateTotal
import pl.expensive.formatValue
import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.WalletsStorage
import pl.expensive.transaction.Header
import pl.expensive.transaction.NewTransactionActivity
import pl.expensive.transaction.TransactionGrouper
import pl.expensive.transaction.TransactionsAdapter
import java.util.*


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

        vTransactions.layoutManager = LinearLayoutManager(this)
        vTransactions.adapter = adapter
        vTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        vNewTransactionHeader.setOnClickListener {
            startActivityForResult(Intent(this@WalletsActivity, NewTransactionActivity::class.java), 666)
        }
    }

    override fun onStart() {
        super.onStart()

        update(ViewState.Loading())
        showWallets()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 666 && resultCode == Activity.RESULT_OK) {

            // No need to refresh adapter. onStart was called and did it

            val storedTransactionAmount = data?.getStringExtra("storedTransaction") ?: ""
            if (storedTransactionAmount.isNotBlank()) {
                toast(getString(R.string.new_transaction_success_message, storedTransactionAmount))
            }
        }
    }

    private fun showWallets() {
        val data = walletStorage.list().first()
        val transactionData = transactionStorage.select().sortedByDescending { it.date }

        if (transactionData.isEmpty()) {
            update(ViewState.Empty())
        } else {
            update(ViewState.Wallets(WalletViewModel(
                    data.name,
                    transactionData.filter { it.wallet == data.uuid },
                    data.currency)))
        }
    }

    private fun update(viewState: ViewState) = when (viewState) {
        is ViewState.Loading -> {
            vTransactions.visibility = GONE
            loading.visibility = VISIBLE
        }
        is ViewState.Wallets -> {
            vTransactions.visibility = VISIBLE
            loading.visibility = GONE

            val result = mutableListOf<Any>()
            val today = LocalDateTime.now()
            TransactionGrouper.group(viewState.viewModels.transactions.filter {
                !it.toLocalDateTime().isAfter(today)
            }).forEach {
                result.add(Header(it.formatHeader(), formattedHeaderTotal(viewState.viewModels.currency, it.value)))
                result.addAll(it.value)
            }

            adapter.data = result
            supportActionBar!!.title = viewState.viewModels.formattedTitle()
        }
        is ViewState.Empty -> {
            loading.visibility = GONE
            vTransactions.visibility = VISIBLE
            // TODO: Add empty view
        }
        is ViewState.Error -> {
            vTransactions.visibility = GONE
            loading.visibility = GONE

            // TODO: show error view here instead toast
            toast(viewState.err)
        }
    }

    /**
     * Creates Spannable with month name at full size,
     * followed by short year  (eg '17 from 2017) with 60% text size (from month name)
     *
     * Year part is skipped for current year
     */
    private fun Map.Entry<YearMonth, List<Transaction>>.formatHeader(): CharSequence {
        val month = key.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize()
        val now = YearMonth.now()
        if (key.year == now.year) {
            if (key.month == now.month) {
                return getString(R.string.current_month)
            }
            return month
        }

        val span = SpannableString("$month '${if (key.year > 2000) key.year - 2000 else key.year}")
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

    private fun formattedHeaderTotal(currency: Currency, transactions: List<Transaction>): Spannable {
        val relative: Spannable = SpannableString(getString(R.string.total))
        relative.setSpan(RelativeSizeSpan(.6f), 0, relative.length, 0)
        return SpannableStringBuilder()
                .append(relative)
                .append(currency.formatValue(money = transactions.calculateTotal()))
    }
}

