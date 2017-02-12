package pl.expensive.transaction

import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import pl.expensive.storage.Transaction
import java.util.*

object TransactionGrouper {
    fun group(sortedTransactions: List<Transaction>): List<Any> {
        val result = mutableListOf<Any>()
        sortedTransactions
                .groupBy { it.toLocalDateTime().toLocalDate() }
                .forEach {
                    result.add(Header(it.formatHeader()))
                    result.addAll(it.value)
                }
        return result
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
}
