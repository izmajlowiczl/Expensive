package pl.expensive.wallet

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
import org.jetbrains.anko.toast
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.TextStyle
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.formatValue
import pl.expensive.hideKeyboard
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.WalletsStorage
import pl.expensive.storage._Seeds
import pl.expensive.transaction.Header
import pl.expensive.transaction.TransactionGrouper
import pl.expensive.transaction.TransactionsAdapter
import java.math.BigDecimal
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

        val vRecycler = findViewById(R.id.transactions) as RecyclerView
        vRecycler.layoutManager = LinearLayoutManager(this)
        vRecycler.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(vRecycler.getContext(),
                LinearLayoutManager.VERTICAL)
        vRecycler.addItemDecoration(dividerItemDecoration)

        vCreateTransaction.setOnClickListener {
            val amountText = vCreateTransactionAmount.text.toString()
            if (amountText.isNullOrEmpty()) {
                vCreateTransactionAmount.error = "Mandatory"
            } else {
                vCreateTransactionAmount.error = null

                update(ViewState.Loading())

                val amount = BigDecimal(amountText)
                val descText = vCreateTransactionDescription.text.toString()
                transactionStorage.insert(Transaction.withAmount(amount = amount, desc = descText))
                vCreateTransactionAmount.text.clear()
                vCreateTransactionDescription.text.clear()
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
            adapter.data = result
            toolbar.title = viewState.viewModels.formattedTitle()
        }
        is ViewState.Error -> {
            transactions.visibility = GONE
            loading.visibility = GONE

            // TODO: show error view here instead toast
            Toast.makeText(this, viewState.err, Toast.LENGTH_SHORT).show()
        }
    }

    private fun Map.Entry<LocalDate, List<Transaction>>.formatHeader(): Spannable {
        val month = key.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val year = if (key.year > 2000) key.year - 2000 else key.year

        val span = SpannableString("$month '$year")
        val start = month.length
        val end = span.length
        span.setSpan(SuperscriptSpan(), start, end, 0)
        span.setSpan(RelativeSizeSpan(.6f), start, end, 0)

        return span
    }

    private fun WalletViewModel.formattedTitle(): Spannable {
        val span: Spannable = SpannableString("$name  ${formattedTotal()}")
        span.setSpan(RelativeSizeSpan(.6f), 0, name.length, 0)
        return span
    }
}

