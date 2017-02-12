package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.view_header_item.view.*

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun update(header: Header) {
        itemView.vHeader.text = header.header
    }
}
