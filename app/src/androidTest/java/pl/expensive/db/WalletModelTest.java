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
public class WalletModelTest {
    Database inMemDatabase;
    WalletModel model;

    @Before
    public void setUp() {
        inMemDatabase = new Database(
                InstrumentationRegistry.getTargetContext(), null);
        model = new WalletModel(inMemDatabase);
    }

    @Test
    public void createAndListWallet() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "Test-Walet");
        model.insert(wallet);

        Collection<Wallet> wallets = model.list();

        assertThat(wallets).containsExactly(wallet);
    }

    @Test(expected = IllegalStateException.class)
    public void storingWalletWithTheSameUuidIsNotAllowed() {
        UUID theSameId = UUID.randomUUID();

        Wallet wallet = new Wallet(theSameId, "Test-Walet");
        model.insert(wallet);

        Wallet otherWallet = new Wallet(theSameId, "Other-Test-Walet");
        model.insert(otherWallet);

        // should never get here
        assertTrue(false);
    }

    @Test(expected = IllegalStateException.class)
    public void storingWalletWithTheSameNameNotAllowed() {
        String theSameName = "the-same-name";

        Wallet wallet = new Wallet(UUID.randomUUID(), theSameName);
        model.insert(wallet);

        Wallet otherWallet = new Wallet(UUID.randomUUID(), theSameName);
        model.insert(otherWallet);

        // should never get here
        assertTrue(false);
    }
}