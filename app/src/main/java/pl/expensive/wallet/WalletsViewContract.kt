package pl.expensive.wallet

import android.support.annotation.MainThread

internal interface WalletsViewContract {
    @MainThread
    fun showWallets(storedWallets: Collection<WalletViewModel>)

    @MainThread
    fun showEmpty()

    @MainThread
    fun showFetchError()
}
