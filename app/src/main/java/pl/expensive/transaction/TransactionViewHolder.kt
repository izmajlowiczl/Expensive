package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
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
            transaction_desc.text = viewModel.description
            transaction_time.text = viewModel.toLocalDateTime().toLocalTime().format(timeFormat)
        }
    }

    private fun Transaction.formatValue(locale: Locale = Locale.getDefault()): String {
        val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
        numberFormat.applyPattern(currency.format)
        return numberFormat.format(amount)
    }
}
