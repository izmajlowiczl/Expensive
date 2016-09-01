package pl.expensive.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseSchemaTest {
    Database database = new Database(
            InstrumentationRegistry.getTargetContext(), null);

    public static List<String> getTableColumns(SQLiteDatabase db, String tableName) {
        Cursor c = null;
        try {
            c = db.rawQuery("pragma table_info(" + tableName + ");", null);

            List<String> columns = new ArrayList<>();
            while (c.moveToNext()) {
                columns.add(c.getString(c.getColumnIndex("name")));
            }

            return columns;
        } catch (Exception e) {
        }

        return Collections.emptyList();
    }

    @Test
    public void walletTableIsCreatedWithAllColumns() {
        List<String> columns = getTableColumns(database.getReadableDatabase(), "tbl_wallet");

        assertThat(columns)
                .containsOnly("uuid", "name");
    }
}
