package pl.expensive

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.Wallet
import pl.expensive.storage.WalletsStorage

internal open class DisplayWallets(private val walletsStorage: WalletsStorage,
                                   private val transactionStorage: TransactionStorage) {
    fun runFor(view: WalletsViewContract) {
        view.display { fetchWallets() }
    }

    fun WalletsViewContract.display(f: () -> Collection<Wallet>) {
        doAsync {
            val viewModels = f()
                    .map {
                        WalletViewModel(
                                it.name,
                                transactionStorage.select(it.uuid) as List<Transaction>,
                                it.currency)
                    }
            uiThread {
                if (viewModels.isEmpty()) {
                    showEmpty()
                } else {
                    showWallets(viewModels)
                }
            }
        }
    }

    private fun fetchWallets(): Collection<Wallet> = walletsStorage.list()
}
