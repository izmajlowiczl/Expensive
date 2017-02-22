package pl.expensive.wallet

import pl.expensive.storage.Wallet
import pl.expensive.storage.WalletsStorage

// TODO: Move to logic module
class WalletsService(private val walletsStorage: WalletsStorage) {
    fun primaryWallet() : Wallet {
        return walletsStorage.list().first()
    }
}

