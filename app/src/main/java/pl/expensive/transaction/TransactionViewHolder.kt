package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.view_transaction_item.view.*
import pl.expensive.dateTimeFormat
import pl.expensive.middleOnScreen
import pl.expensive.storage.Transaction
import java.text.DecimalFormat
import java.util.*

class TransactionViewHolder(itemView: View, private val clickFun: (IntArray, Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update(viewModel: Transaction) = with(itemView) {
        vTransactionItemAmount.text = viewModel.formatAmount()
        vTransactionItemTime.text = viewModel.formatTime()
        if (viewModel.description.isNullOrBlank()) {
            vTransactionItemDesc.visibility = GONE
        } else {
            vTransactionItemDesc.visibility = VISIBLE
            vTransactionItemDesc.text = viewModel.description
        }

        setOnClickListener { clickFun(middleOnScreen(), viewModel) }
    }

    /**
     * Formats transaction time to show date (eg Jan 12) in normal text size.
     * Time (22:12) in 70% of date text size in new line
     */
    private fun Transaction.formatTime(): CharSequence {
        val localDateTime = toLocalDateTime()
        return localDateTime.format(dateTimeFormat)
    }

    /**
     * Formats transaction's amount to use localised currency symbol
     */
    private fun Transaction.formatAmount(locale: Locale = Locale.getDefault()): String {
        val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
        numberFormat.applyPattern(currency.format)
        return numberFormat.format(amount)
    }
}
