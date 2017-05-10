package pl.expensive.transaction.list

import android.content.SharedPreferences
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import pl.expensive.R
import pl.expensive.calculateTotal
import pl.expensive.formatValue
import pl.expensive.storage.*
import pl.expensive.storage.Currency
import java.math.BigDecimal
import java.util.*

class TransactionsModel(private val transactionStorage: TransactionStorage,
                        private val res: Resources,
                        private val prefs: SharedPreferences) {

    fun showWallets(viewCallback: (ViewState) -> Unit) {
        val transactionData = transactionStorage.list().sortedByDescending { it.date }

        val currency = getDefaultCurrency(prefs)
        val viewState = if (transactionData.isEmpty()) {
            ViewState.Empty(formattedScreenTitle(BigDecimal.ZERO, currency))
        } else {
            val today = LocalDateTime.now()
            val transactionsUntilToday = transactionData.filter { !it.toLocalDateTime().isAfter(today) }

            // Transactions grouped by month with headers
            val result = mutableListOf<Any>()
            group(transactionsUntilToday)
                    .forEach {
                        if (it.key == YearMonth.from(today)) { // This month
                            result.addAll(it.value)
                        } else {
                            result.add(Header(it.formattedHeaderTitle(res), formattedHeaderTotal(res, currency, it.value)))
                        }
                    }

            // Month name with total
            val title = formattedScreenTitle(transactionsUntilToday
                    .filter { YearMonth.from(it.toLocalDateTime()) == YearMonth.from(today) }
                    .calculateTotal(), currency)

            ViewState.Wallets(result, title)
        }

        viewCallback(viewState)
    }

    /**
     * Creates Spannable with month name at full size,
     * followed by short year  (eg '17 from 2017) with 60% text size (from month name)
     *
     * Year part is skipped for current year
     */
    private fun Map.Entry<YearMonth, List<Transaction>>.formattedHeaderTitle(res: Resources): CharSequence {
        val month = key.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize()
        val now = YearMonth.now()
        if (key.year == now.year) {
            if (key.month == now.month) {
                return res.getString(R.string.current_month)
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
     * Creates Spannable with total amount of money for month in format Total: amount.
     *
     * Total prefix is 60% font size of amount
     */
    private fun formattedHeaderTotal(res: Resources, currency: Currency, transactions: List<Transaction>): Spannable {
        val relative: Spannable = SpannableString(res.getString(R.string.total))
        relative.setSpan(RelativeSizeSpan(.6f), 0, relative.length, 0)
        return SpannableStringBuilder()
                .append(relative)
                .append(currency.formatValue(money = transactions.calculateTotal()))
    }

    /**
     * Generates title for screen by showing current month name with total amount per current month
     */
    private fun formattedScreenTitle(total: BigDecimal, currency: Currency): CharSequence {
        val month = YearMonth.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize()

        val span = SpannableString("$month ${currency.formatValue(money = total)}")
        span.setSpan(RelativeSizeSpan(.6f), 0, month.length, 0)

        return span
    }
}

fun group(sortedTransactions: List<Transaction>): Map<YearMonth, List<Transaction>> {
    return sortedTransactions.groupBy { YearMonth.of(it.toLocalDateTime().year, it.toLocalDateTime().month) }
}
