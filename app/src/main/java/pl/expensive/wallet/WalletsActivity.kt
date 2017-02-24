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
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.activity_wallets.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import pl.expensive.*
import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.transaction.Header
import pl.expensive.transaction.NewTransactionActivity
import pl.expensive.transaction.TransactionGrouper
import pl.expensive.transaction.TransactionsAdapter
import java.util.*

class WalletsActivity : AppCompatActivity() {
    private val walletService: WalletsService by lazy { Injector.app().walletsService() }
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }

    private val adapter by lazy {
        TransactionsAdapter({ touchPos, transition ->
            startEditTransactionScreen(touchPos, transition)
        })
    }

    private var shouldAnimateFab = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)
        Injector.app().inject(this)

        // Animate FAB only once
        shouldAnimateFab = savedInstanceState == null

        setSupportActionBar(toolbar)

        vTransactions.layoutManager = LinearLayoutManager(this)
        vTransactions.adapter = adapter
        vTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        startContentAnimation()
    }

    private fun startContentAnimation() {
        val fabClickListener: (View) -> Unit = {
            startNewTransactionCreatorScreen()
        }
        with(vCreateTransactionFab) {
            if (shouldAnimateFab) {
                translationY = 2 * resources.getDimension(R.dimen.fab_size) // Hide below screen
                animate() // Pop up from bottom
                        .translationY(0f)
                        .setInterpolator(OvershootInterpolator(1f))
                        .setStartDelay(300)
                        .setDuration(longAnim().toLong())
                        .withEndAction { setOnClickListener(fabClickListener) }
                        .start()
            } else {
                show(true)
                setOnClickListener(fabClickListener)
            }
        }
    }

    private fun startNewTransactionCreatorScreen() {
        val intent = Intent(this@WalletsActivity, NewTransactionActivity::class.java)
                .putExtra("loc", vCreateTransactionFab.middleOnScreen())
        startActivityForResult(intent, 666)
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        showWallets()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 666 && resultCode == Activity.RESULT_OK) {

            // No need to refresh adapter. onStart was called and did it

            // TODO("Instead of passing string, pass value. Show deposit/withdrawal message")
            val storedTransactionAmount = data?.getStringExtra("storedTransaction") ?: ""
            if (storedTransactionAmount.isNotBlank()) {
                toast(getString(R.string.new_transaction_success_message, storedTransactionAmount))
            }
        }
    }

    private fun showWallets() {
        val wallet = walletService.primaryWallet()
        val transactionData = transactionStorage.select().sortedByDescending { it.date }

        val viewState = if (transactionData.isEmpty()) {
            ViewState.Empty(formattedScreenTitle(emptyList()))
        } else {
            val today = LocalDateTime.now()
            val transactionsUntilToday = transactionData.filter { !it.toLocalDateTime().isAfter(today) }

            // Transactions grouped by month with headers
            val result = mutableListOf<Any>()
            TransactionGrouper.group(transactionsUntilToday)
                    .forEach {
                        if (it.key == YearMonth.from(today)) { // This month
                            result.addAll(it.value)
                        } else {
                            result.add(Header(it.formattedHeaderTitle(), formattedHeaderTotal(wallet.currency, it.value)))
                        }
                    }

            // Month name with total
            val title = formattedScreenTitle(transactionsUntilToday.filter {
                YearMonth.from(it.toLocalDateTime()) == YearMonth.from(today)
            })

            ViewState.Wallets(result, title)
        }

        update(viewState)
    }

    private fun update(viewState: ViewState) {
        when (viewState) {
            is ViewState.Wallets -> {
                vTransactions.visibility = VISIBLE

                adapter.data = viewState.adapterData
                supportActionBar!!.title = viewState.title
            }

            is ViewState.Empty -> {
                vTransactions.visibility = VISIBLE

                adapter.data = mutableListOf<Any>()
                supportActionBar!!.title = viewState.title
            }
        }
    }

    /**
     * Creates Spannable with month name at full size,
     * followed by short year  (eg '17 from 2017) with 60% text size (from month name)
     *
     * Year part is skipped for current year
     */
    private fun Map.Entry<YearMonth, List<Transaction>>.formattedHeaderTitle(): CharSequence {
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

    private fun formattedHeaderTotal(currency: Currency, transactions: List<Transaction>): Spannable {
        val relative: Spannable = SpannableString(getString(R.string.total))
        relative.setSpan(RelativeSizeSpan(.6f), 0, relative.length, 0)
        return SpannableStringBuilder()
                .append(relative)
                .append(currency.formatValue(money = transactions.calculateTotal()))
    }

    private fun formattedScreenTitle(transactions: List<Transaction>): CharSequence {
        val month = YearMonth.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize()

        val currentWallet = walletService.primaryWallet()
        val span = SpannableString("$month ${currentWallet.currency.formatValue(money = transactions.calculateTotal())}")
        span.setSpan(RelativeSizeSpan(.6f), 0, month.length, 0)

        return span
    }
}

