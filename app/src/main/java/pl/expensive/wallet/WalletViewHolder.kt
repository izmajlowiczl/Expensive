package pl.expensive.wallet

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.view_wallet_item.view.*

internal class WalletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(viewModel: WalletViewModel) {
        itemView.wallet_name.text = viewModel.name
        itemView.wallet_total.text = viewModel.formattedTotal()
    }
}
