package pl.expensive.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;
import static java.util.UUID.randomUUID;
import static pl.expensive.storage.DatabaseSchemaTestHelper.storeWalletSql;
import static pl.expensive.storage._Seeds.CASH;

@RunWith(AndroidJUnit4.class)
public class ListWalletsStorageTest {
    private WalletsStorage storage;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        Database inMemDatabase = Injector.provideDatabase();
        db = inMemDatabase.getWritableDatabase();
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
        Wallet bankWallet = Wallet.create(randomUUID(), "bank");
        Wallet ccWallet = Wallet.create(randomUUID(), "credit card");
        db.execSQL(storeWalletSql(bankWallet));
        db.execSQL(storeWalletSql(ccWallet));

        Collection<Wallet> wallets = storage.list();

        assertThat(wallets)
                .containsExactly(CASH, bankWallet, ccWallet);
    }
}
