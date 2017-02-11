package pl.expensive.wallet

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.expensive.Injector
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.Wallet
import pl.expensive.storage.WalletsStorage
import pl.expensive.wallet.ViewState.*

internal open class DisplayWallets(private val walletsStorage: WalletsStorage = Injector.app().wallets(),
                                   private val transactionStorage: TransactionStorage = Injector.app().transactions()) {
    fun runFor(view: WalletsViewContract) {
        view.update(Loading())

        val exceptionHandler: (Throwable) -> Unit = {
            view.update(Error("Cannot Load wallets"))
        }

        doAsync(exceptionHandler) {
            val viewModels = walletsStorage.list().map { it.toViewModel() }
            uiThread {
                view.update(if (viewModels.isNotEmpty()) Wallets(viewModels) else Empty())
            }
        }
    }

    private fun Wallet.toViewModel(): WalletViewModel {
        return WalletViewModel(
                name,
                transactionStorage.select(uuid),
                currency)
    }
}
