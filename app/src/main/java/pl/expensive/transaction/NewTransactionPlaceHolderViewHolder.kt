package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View

class NewTransactionPlaceHolderViewHolder(itemView: View, val itemClick: () -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update() = itemView.setOnClickListener { itemClick() }
}

