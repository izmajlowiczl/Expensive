package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.expensive.R
import pl.expensive.inflateLayout
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import kotlin.properties.Delegates

class TransactionsAdapter(val transactionStorage: TransactionStorage,
                          val afterTransactionStoredCallback: (transaction: Transaction) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: MutableList<Any> by Delegates.vetoable(mutableListOf()) { p, old, new ->
        notifyDataSetChanged()
        old != new
    }

    fun plus(transaction: Transaction) {
        val pos = calculatePosForNewTransaction(transaction)
        data.add(pos, transaction)
        notifyItemInserted(pos)
    }

    private fun calculatePosForNewTransaction(transaction: Transaction): Int {
        // Add just below placeholder and header
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                R.layout.view_transaction_item ->
                    TransactionViewHolder(parent.inflateLayout(viewType))
                R.layout.view_header_item ->
                    HeaderViewHolder(parent.inflateLayout(viewType))
                R.layout.view_new_transaction_placeholder_item ->
                    NewTransactionPlaceHolderViewHolder(
                            parent.inflateLayout(viewType),
                            transactionStorage,
                            afterTransactionStoredCallback)
                else ->
                    throw IllegalArgumentException("Unknown view type $viewType")
            }


    override fun getItemViewType(position: Int): Int =
            when (data[position]) {
                is Transaction ->
                    R.layout.view_transaction_item
                is Header ->
                    R.layout.view_header_item
                is NewTransactionPlaceHolder ->
                    R.layout.view_new_transaction_placeholder_item
                else -> -1
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            when (holder) {
                is TransactionViewHolder ->
                    holder.update(data[position] as Transaction)
                is HeaderViewHolder ->
                    holder.update(data[position] as Header)
                is NewTransactionPlaceHolderViewHolder ->
                    holder.update()
                else ->
                    throw IllegalArgumentException("Unknown view type ${holder.itemViewType}")
            }

    override fun getItemCount(): Int = data.size
}
