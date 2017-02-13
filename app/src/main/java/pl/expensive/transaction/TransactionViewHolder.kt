package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.view_transaction_item.view.*
import org.threeten.bp.format.DateTimeFormatter
import pl.expensive.storage.Transaction
import java.text.DecimalFormat
import java.util.*

class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun update(viewModel: Transaction) = with(itemView) {
        transaction_amount.text = viewModel.formatAmount()
        transaction_time.text = viewModel.formatTime()
        if (viewModel.description.isNullOrBlank()) {
            transaction_desc.visibility = GONE
        } else {
            transaction_desc.visibility = VISIBLE
            transaction_desc.text = viewModel.description
        }
    }

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormat = DateTimeFormatter.ofPattern("MMM d")

    /**
     * Formats transaction time to show date (eg Jan 12) in normal text size.
     * Time (22:12) in 70% of date text size in new line
     */
    private fun Transaction.formatTime(): CharSequence {
        val localDateTime = toLocalDateTime()
        val format = localDateTime.format(dateTimeFormat)
        val span = SpannableString("$format \n ${localDateTime.format(timeFormat)}")
        span.setSpan(RelativeSizeSpan(.7f), span.length - format.length, span.length, 0)
        return span
    }

    private fun Transaction.formatAmount(locale: Locale = Locale.getDefault()): String {
        val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
        numberFormat.applyPattern(currency.format)
        return numberFormat.format(amount)
    }
}
