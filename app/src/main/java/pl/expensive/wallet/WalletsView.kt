package pl.expensive.wallet

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

internal class WalletsView : RecyclerView {
    private var walletsAdapter: WalletsAdapter = WalletsAdapter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = walletsAdapter
    }

    fun update(wallets: WalletViewModel) {
        walletsAdapter.bind(listOf(wallets))
    }
}
