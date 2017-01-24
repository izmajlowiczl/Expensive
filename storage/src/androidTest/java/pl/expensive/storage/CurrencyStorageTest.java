package pl.expensive.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored;

@RunWith(AndroidJUnit4.class)
public class CurrencyStorageTest {
    private CurrencyStorage storage;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        Database inMemDatabase = Injector.provideDatabase();
        db = inMemDatabase.getWritableDatabase();
        storage = new SQLiteBasedCurrencyStorage(inMemDatabase);
    }

    @Test
    public void storeSingleCurrency() {
        Currency currency = Currency.create("FAKE", "$#.##");

        storage.insert(currency);

        assertCurrencyCodeStored(db, "FAKE");
    }

    @Test(expected = IllegalStateException.class)
    public void storeDuplicates() {
        Currency currency = Currency.create("FAKE", "$#.##");
        
        storage.insert(currency);
        storage.insert(currency);
    }
}
