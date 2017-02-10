package pl.expensive.wallet

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
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

    override fun update(viewState: ViewState) {
        when (viewState) {
            is ViewState.Loading -> {
            }
            is ViewState.Wallets -> {
                visibility = View.VISIBLE
                walletsAdapter.bind(viewState.viewModels)
            }
            is ViewState.Empty -> {
                visibility = GONE
            }
            is ViewState.Error -> {
                Toast.makeText(context, viewState.err, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
