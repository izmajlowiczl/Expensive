package pl.expensive.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.fail;
import static pl.expensive.storage.DatabaseSchemaTestHelper.assertCurrencyCodeStored;

@RunWith(AndroidJUnit4.class)
public class DatabaseSeedsTest {
    Database database;
    SQLiteDatabase db;

    @Before
    public void setup() {
        database = Injector.provideDatabase();
        db = database.getReadableDatabase();
    }

    @Test
    public void containsPrePopulatedCashWallet() {
        Cursor cursor = db.rawQuery("SELECT name FROM tbl_wallet", null);
        if (cursor == null || !cursor.moveToNext()) {
            fail("Cannot find pre populated cash row");
        }

        assertThat(cursor.getString(0))
                .isEqualTo("Cash");
    }

    @Test
    public void containsEurCurrency() {
        assertCurrencyCodeStored(db, "EUR");
    }

    @Test
    public void containsGBPCurrency() {
        assertCurrencyCodeStored(db, "GBP");
    }

    @Test
    public void containsCHFCurrency() {
        assertCurrencyCodeStored(db, "CHF");
    }

    @Test
    public void containsPLNCurrency() {
        assertCurrencyCodeStored(db, "PLN");
    }

    @Test
    public void containsCZKCurrency() {
        assertCurrencyCodeStored(db, "CZK");
    }
}
