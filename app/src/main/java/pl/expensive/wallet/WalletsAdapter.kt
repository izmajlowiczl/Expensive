package pl.expensive.wallet

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.expensive.R
import pl.expensive.inflateLayout
import java.util.*

internal class WalletsAdapter : RecyclerView.Adapter<WalletViewHolder>() {
    private val wallets = ArrayList<WalletViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        return WalletViewHolder(parent.inflateLayout(R.layout.view_wallet_item))
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) =
            holder.bind(wallets[position])


    override fun getItemCount(): Int =
            wallets.size


    fun bind(newWallets: List<WalletViewModel>) {
        wallets.clear()
        wallets.addAll(newWallets)
        notifyDataSetChanged()
    }
}
