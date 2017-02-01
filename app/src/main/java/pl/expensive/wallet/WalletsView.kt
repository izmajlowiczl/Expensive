package pl.expensive.wallet

import android.content.Context
import android.support.annotation.MainThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.Toast

internal class WalletsView : RecyclerView, WalletsViewContract {
    private var walletsAdapter: WalletsAdapter = WalletsAdapter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = walletsAdapter
    }

    @MainThread
    override fun showWallets(storedWallets: Collection<WalletViewModel>) {
        walletsAdapter.bind(storedWallets)
    }

    @MainThread
    override fun showEmpty() {
        visibility = GONE
    }

    @MainThread
    override fun showFetchError() {
        Toast.makeText(context, "Cannot fetch wallets", Toast.LENGTH_SHORT).show()
    }
}
