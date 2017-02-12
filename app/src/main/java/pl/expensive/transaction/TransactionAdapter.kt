package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.expensive.R
import pl.expensive.inflateLayout
import pl.expensive.storage.Transaction
import kotlin.properties.Delegates

class TransactionsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.view_transaction_item ->
                return TransactionViewHolder(parent.inflateLayout(R.layout.view_transaction_item))
            R.layout.view_header_item ->
                return HeaderViewHolder(parent.inflateLayout(R.layout.view_header_item))
            else ->
                throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    var data: List<Any> by Delegates.vetoable(emptyList()) { p, old, new ->
        notifyDataSetChanged()
        old != new
    }

    override fun getItemViewType(position: Int): Int {
        val any = data[position]
        when (any) {
            is Transaction ->
                return R.layout.view_transaction_item
            is Header ->
                return R.layout.view_header_item
            else ->
                return -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionViewHolder ->
                holder.update(data[position] as Transaction)
            is HeaderViewHolder ->
                holder.update(data[position] as Header)
            else ->
                throw IllegalArgumentException("Unknown view type ${holder.itemViewType}")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
