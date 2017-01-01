package pl.expensive.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseSchemaTestHelper {

    public static List<String> getTableColumns(SQLiteDatabase db, String tableName) {
        Cursor c = null;
        try {
            c = db.rawQuery("pragma table_info(" + tableName + ");", null);

            List<String> columns = new ArrayList<>();
            while (c.moveToNext()) {
                columns.add(c.getString(c.getColumnIndex("name")));
            }

            c.close();
            return columns;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }
}
