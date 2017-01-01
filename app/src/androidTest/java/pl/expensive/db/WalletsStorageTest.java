package pl.expensive.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class WalletsStorageTest {
    Database inMemDatabase;
    SQLiteBasedWalletsStorage model;

    @Before
    public void setUp() {
        inMemDatabase = new Database(
                InstrumentationRegistry.getTargetContext(), null);
        model = new SQLiteBasedWalletsStorage(inMemDatabase);
    }

    @Test
    public void createAndListWallet() {
        Wallet wallet = Wallet.create(UUID.randomUUID(), "Test-Walet");
        model.insert(wallet);

        Collection<Wallet> wallets = model.list();

        assertThat(wallets).containsExactly(wallet);
    }

    @Test(expected = IllegalStateException.class)
    public void storingWalletWithTheSameUuidIsNotAllowed() {
        UUID theSameId = UUID.randomUUID();

        Wallet wallet = Wallet.create(theSameId, "Test-Walet");
        model.insert(wallet);

        Wallet otherWallet = Wallet.create(theSameId, "Other-Test-Walet");
        model.insert(otherWallet);

        // should never get here
        assertTrue(false);
    }

    @Test(expected = IllegalStateException.class)
    public void storingWalletWithTheSameNameNotAllowed() {
        String theSameName = "the-same-name";

        Wallet wallet = Wallet.create(UUID.randomUUID(), theSameName);
        model.insert(wallet);

        Wallet otherWallet = Wallet.create(UUID.randomUUID(), theSameName);
        model.insert(otherWallet);

        // should never get here
        assertTrue(false);
    }
}