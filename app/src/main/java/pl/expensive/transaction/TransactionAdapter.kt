package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.expensive.R
import pl.expensive.inflateLayout
import pl.expensive.storage.Transaction
import kotlin.properties.Delegates

class TransactionsAdapter : RecyclerView.Adapter<TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(parent.inflateLayout(R.layout.view_transaction_item))
    }

    var data: List<Transaction> by Delegates.vetoable(emptyList()) { p, old, new ->
        notifyDataSetChanged()
        old != new
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.update(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
