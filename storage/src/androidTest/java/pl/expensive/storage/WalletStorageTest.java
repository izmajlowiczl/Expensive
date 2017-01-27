package pl.expensive.storage;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;
import static java.util.UUID.randomUUID;
import static pl.expensive.storage._Seeds.CASH;
import static pl.expensive.storage._Seeds.EUR;
import static pl.expensive.storage._Seeds.PLN;

@RunWith(AndroidJUnit4.class)
public class WalletStorageTest {
    private WalletsStorage storage;

    @Before
    public void setUp() {
        Database inMemDatabase = Injector.provideDatabase();
        storage = new SQLiteBasedWalletsStorage(inMemDatabase);
    }

    @Test
    public void prePopulatedWallet() {
        Collection<Wallet> wallets = storage.list();

        assertThat(wallets)
                .containsExactly(CASH);
    }

    @Test
    public void listMultipleWallets() {
        Wallet bankWallet = Wallet.create(randomUUID(), "bank", EUR);
        Wallet ccWallet = Wallet.create(randomUUID(), "credit card", EUR);
        storage.insert(bankWallet);
        storage.insert(ccWallet);

        Collection<Wallet> wallets = storage.list();

        assertThat(wallets)
                .containsExactly(CASH, bankWallet, ccWallet);
    }

    @Test
    public void storeSingleWallet() {
        Wallet bankWallet = Wallet.create(randomUUID(), "bank", PLN);
        storage.insert(bankWallet);

        assertThat(storage.list())
                .containsExactly(CASH, bankWallet);
    }

    @Test
    public void storeMultipleWallets() {
        Wallet bankWallet = Wallet.create(randomUUID(), "bank", PLN);
        Wallet ccWallet = Wallet.create(randomUUID(), "credit card", PLN);
        storage.insert(bankWallet);
        storage.insert(ccWallet);

        assertThat(storage.list())
                .containsExactly(CASH, bankWallet, ccWallet );
    }

    @Test(expected = IllegalStateException.class)
    public void storeDuplicates() {
        Wallet bankWallet = Wallet.create(randomUUID(), "bank", PLN);
        storage.insert(bankWallet);
        storage.insert(bankWallet);
    }
}
