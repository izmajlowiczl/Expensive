package pl.expensive.storage;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DatabaseSeedsTest {
    private Database database = Injector.provideDatabase();

    @Test
    public void containsPrePopulatedCashWallet() {
        Cursor cursor = database.getWritableDatabase().rawQuery("SELECT name FROM tbl_wallet", null);
        if (cursor == null || !cursor.moveToNext()) {
            fail("Cannot find pre populated cash row");
        }

        assertThat(cursor.getString(0))
                .isEqualTo("cash");
    }
}
