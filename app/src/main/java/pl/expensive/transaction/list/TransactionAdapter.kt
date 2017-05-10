package pl.expensive.transaction.list

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.expensive.R
import pl.expensive.inflateLayout
import pl.expensive.storage.Transaction
import kotlin.properties.Delegates

class TransactionsAdapter(private val transactionClickFun: (IntArray, Transaction) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
