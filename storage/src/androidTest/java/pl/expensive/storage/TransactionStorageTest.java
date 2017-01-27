package pl.expensive.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static pl.expensive.storage._Seeds.CASH;
import static pl.expensive.storage._Seeds.EUR;

@RunWith(AndroidJUnit4.class)
public class TransactionStorageTest {
    SQLiteDatabase db;
    TransactionStorage storage;

    @Before
    public void setUp() {
        Database inMemDatabase = Injector.provideDatabase();
        db = inMemDatabase.getWritableDatabase();
        storage = new SQLiteBasedTransactionStorage(inMemDatabase);
    }

    @Test
    public void storeSingleTransaction() {
        Transaction beer = Transaction.create(UUID.randomUUID(), CASH.uuid(), new BigDecimal("4.99"), CASH.currency(), new Date().getTime(), "Beer");

        storage.insert(beer);

        assertThat(storage.select(CASH.uuid()))
                .containsExactly(beer);
    }

    @Test
    public void selectTransaction() {
        Transaction beer = Transaction.create(UUID.randomUUID(), CASH.uuid(), new BigDecimal("4.99"), CASH.currency(), new Date().getTime(), "Beer");
        storage.insert(beer);

        assertThat(storage.select(CASH.uuid()))
                .containsExactly(beer);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listingAllTransactionsIsNotSupported() {
        storage.list();
    }

    @Test(expected = IllegalStateException.class)
    public void withoutStoredWallet() {
        Transaction beer = Transaction.create(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("4.99"), EUR, new Date().getTime(), "Beer");

        storage.insert(beer);
    }

    @Test(expected = IllegalStateException.class)
    public void withoutStoredCurrency() {
        Transaction beer = Transaction.create(UUID.randomUUID(), CASH.uuid(), new BigDecimal("4.99"), Currency.create("NOT_EXISTING_CURRENCY", "#.###"), new Date().getTime(), "Beer");

        storage.insert(beer);
    }
}
