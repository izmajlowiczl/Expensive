package pl.expensive.transaction.list

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_header_item.view.*
import kotlinx.android.synthetic.main.view_transaction_item.view.*
import org.threeten.bp.YearMonth
import pl.expensive.R
import pl.expensive.dateTimeFormat
import pl.expensive.formatValue
import pl.expensive.inflateLayout
import pl.expensive.storage.TransactionDbo

// Header

data class Header(val header: CharSequence,
                  val formattedTotal: CharSequence,
                  val date: YearMonth,
                  val clickable: Boolean)

fun clickableHeader(header: CharSequence, formattedTotal: CharSequence, date: YearMonth) = Header(header, formattedTotal, date, true)
fun staticHeader(header: CharSequence, formattedTotal: CharSequence, date: YearMonth) = Header(header, formattedTotal, date, false)

class HeaderViewHolder(itemView: View, private val headerClickFun: (YearMonth) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update(header: Header) = with(itemView) {
        vHeader.text = header.header
        vHeaderAmount.text = header.formattedTotal
        if (header.clickable) {
            setOnClickListener { headerClickFun(header.date) }
        }
    }
}

// TransactionDbo

// TODO: Do not use TransactionDbo directly.. create viewModel for it
class TransactionViewHolder(itemView: View, private val clickFun: (TransactionDbo) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update(viewModel: TransactionDbo) = with(itemView) {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
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
    private fun TransactionDbo.formatTime(): CharSequence {
        val localDateTime = toLocalDateTime()
        return localDateTime.format(dateTimeFormat)
    }
}

class TransactionsAdapter(private val transactionClickFun: (TransactionDbo) -> Unit,
                          private val headerClickFun: (YearMonth) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: MutableList<Any> = mutableListOf()

    fun replaceAll(newData: List<Any>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun addTop(transaction: TransactionDbo) {
        data.add(1, transaction)
        notifyItemInserted(1)
    }

    fun edit(transaction: TransactionDbo) {
        val position = data.indexOf(transaction)
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                R.layout.view_transaction_item ->
                    TransactionViewHolder(parent.inflateLayout(viewType), transactionClickFun)
                R.layout.view_header_item ->
                    HeaderViewHolder(parent.inflateLayout(viewType), headerClickFun)
                else ->
                    throw IllegalArgumentException("Unknown view type $viewType")
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            when (holder) {
                is TransactionViewHolder ->
                    holder.update(data[position] as TransactionDbo)
                is HeaderViewHolder ->
                    holder.update(data[position] as Header)
                else ->
                    throw IllegalArgumentException("Unknown view type ${holder.itemViewType}")
            }


    override fun getItemViewType(position: Int): Int =
            when (data[position]) {
                is TransactionDbo ->
                    R.layout.view_transaction_item
                is Header ->
                    R.layout.view_header_item
                else -> -1
            }

    override fun getItemCount(): Int = data.size
}
