package pl.expensive.transaction.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_header_item.view.*
import kotlinx.android.synthetic.main.view_transaction_item.view.*
import pl.expensive.R
import pl.expensive.dateTimeFormat
import pl.expensive.formatValue
import pl.expensive.inflateLayout
import pl.expensive.storage.Transaction
import pl.expensive.storage.toLocalDateTime
import kotlin.properties.Delegates

// Header

data class Header(val header: CharSequence, val formattedTotal: CharSequence)

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun update(header: Header) = with(itemView) {
        vHeader.text = header.header
        vHeaderAmount.text = header.formattedTotal
    }
}

// Transaction

// TODO: Do not use Transaction directly.. create viewModel for it
class TransactionViewHolder(itemView: View, private val clickFun: (Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update(viewModel: Transaction) = with(itemView) {
        vTransactionItemAmount.text = viewModel.currency.formatValue(money = viewModel.amount)
        vTransactionItemTime.text = viewModel.formatTime()
        if (viewModel.description.isNullOrBlank()) {
            vTransactionItemDesc.visibility = GONE
        } else {
            vTransactionItemDesc.visibility = VISIBLE
            vTransactionItemDesc.text = viewModel.description
        }

        setOnClickListener { clickFun(viewModel) }
    }

    /**
     * Formats transaction time to show date (eg Jan 12) in normal text size.
     * Time (22:12) in 70% of date text size in new line
     */
    private fun Transaction.formatTime(): CharSequence {
        val localDateTime = toLocalDateTime()
        return localDateTime.format(dateTimeFormat)
    }
}

class TransactionsAdapter(private val transactionClickFun: (Transaction) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: MutableList<Any> by Delegates.vetoable(mutableListOf()) { p, old, new ->
        notifyDataSetChanged()
        old != new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                R.layout.view_transaction_item ->
                    TransactionViewHolder(parent.inflateLayout(viewType), transactionClickFun)
                R.layout.view_header_item ->
                    HeaderViewHolder(parent.inflateLayout(viewType))
                else ->
                    throw IllegalArgumentException("Unknown view type $viewType")
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            when (holder) {
                is TransactionViewHolder ->
                    holder.update(data[position] as Transaction)
                is HeaderViewHolder ->
                    holder.update(data[position] as Header)
                else ->
                    throw IllegalArgumentException("Unknown view type ${holder.itemViewType}")
            }


    override fun getItemViewType(position: Int): Int =
            when (data[position]) {
                is Transaction ->
                    R.layout.view_transaction_item
                is Header ->
                    R.layout.view_header_item
                else -> -1
            }

    override fun getItemCount(): Int = data.size
}
