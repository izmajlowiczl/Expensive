package pl.expensive.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static java.util.UUID.randomUUID;
import static pl.expensive.storage.Database.DEFAULT_WALLET;
import static pl.expensive.storage.DatabaseSchemaTestHelper.getStoredWalletNames;

@RunWith(AndroidJUnit4.class)
public class InsertWalletStorageTest {
    private WalletsStorage storage;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        Database inMemDatabase = Injector.provideDatabase();
        db = inMemDatabase.getWritableDatabase();
        storage = new SQLiteBasedWalletsStorage(inMemDatabase);
    }

    @Test
    public void storeSingleWallet() {
        Wallet bankWallet = Wallet.create(randomUUID(), "bank");
        storage.insert(bankWallet);

        assertThat(getStoredWalletNames(db))
                .containsExactly(DEFAULT_WALLET.name(), "bank");
    }

    @Test
    public void storeMultipleWallets() {
        storage.insert(Wallet.create(randomUUID(), "bank"));
        storage.insert(Wallet.create(randomUUID(), "credit card"));

        assertThat(getStoredWalletNames(db))
                .containsExactly(DEFAULT_WALLET.name(), "bank", "credit card");
    }

    @Test(expected = IllegalStateException.class)
    public void storeDuplicates() {
        Wallet bankWallet = Wallet.create(randomUUID(), "bank");
        storage.insert(bankWallet);
        storage.insert(bankWallet);
    }
}