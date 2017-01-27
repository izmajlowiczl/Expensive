package pl.expensive.storage

import android.support.test.runner.AndroidJUnit4

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.truth.Truth.assertThat
import java.util.UUID.randomUUID
import pl.expensive.storage._Seeds.CASH
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.PLN

@RunWith(AndroidJUnit4::class)
class WalletStorageTest {
    lateinit var storage: WalletsStorage

    @Before
    fun setUp() {
        val inMemDatabase = Injector.provideDatabase()
        storage = SQLiteBasedWalletsStorage(inMemDatabase)
    }

    @Test
    fun prePopulatedWallet() {
        val wallets = storage.list()

        assertThat(wallets)
                .containsExactly(CASH)
    }

    @Test
    fun listMultipleWallets() {
        val bankWallet = Wallet(randomUUID(), "bank", EUR)
        val ccWallet = Wallet(randomUUID(), "credit card", EUR)
        storage.insert(bankWallet)
        storage.insert(ccWallet)

        val wallets = storage.list()

        assertThat(wallets)
                .containsExactly(CASH, bankWallet, ccWallet)
    }

    @Test
    fun storeSingleWallet() {
        val bankWallet = Wallet(randomUUID(), "bank", PLN)
        storage!!.insert(bankWallet)

        assertThat(storage!!.list())
                .containsExactly(CASH, bankWallet)
    }

    @Test
    fun storeMultipleWallets() {
        val bankWallet = Wallet(randomUUID(), "bank", PLN)
        val ccWallet = Wallet(randomUUID(), "credit card", PLN)
        storage.insert(bankWallet)
        storage.insert(ccWallet)

        assertThat(storage!!.list())
                .containsExactly(CASH, bankWallet, ccWallet)
    }

    @Test(expected = IllegalStateException::class)
    fun storeDuplicates() {
        val bankWallet = Wallet(randomUUID(), "bank", PLN)
        storage.insert(bankWallet)
        storage.insert(bankWallet)
    }
}
