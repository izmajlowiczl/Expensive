package pl.expensive.wallet

import android.support.annotation.MainThread

internal interface WalletsViewContract {
    @MainThread
    fun update(viewState: ViewState)
}
