package pl.expensive.wallet

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.expensive.R
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


    fun bind(newWallets: Collection<WalletViewModel>) {
        wallets.clear()
        wallets.addAll(newWallets)
        notifyDataSetChanged()
    }

    fun ViewGroup.inflateLayout(@LayoutRes layout: Int): View =
            LayoutInflater.from(context).inflate(layout, this, false)
}
