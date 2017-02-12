package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.view_transaction_item.view.*
import org.threeten.bp.format.DateTimeFormatter
import pl.expensive.storage.Transaction
import java.text.DecimalFormat
import java.util.*

class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    fun update(viewModel: Transaction) {
        with(itemView) {
            transaction_amount.text = viewModel.formatValue()
            transaction_time.text = viewModel.toLocalDateTime().toLocalTime().format(timeFormat)
            if (viewModel.description.isNullOrBlank()) {
                transaction_desc.visibility = GONE
            } else {
                transaction_desc.visibility = VISIBLE
                transaction_desc.text = viewModel.description
            }
        }
    }

    private fun Transaction.formatValue(locale: Locale = Locale.getDefault()): String {
        val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
        numberFormat.applyPattern(currency.format)
        return numberFormat.format(amount)
    }
}
