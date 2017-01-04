package pl.expensive.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

class DatabaseSchemaTestHelper {

    static List<String> getTableColumns(SQLiteDatabase db, String tableName) {
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

    static List<String> getStoredWalletNames(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(selectNameFrom("tbl_wallet"), null);
        if (cursor == null) {
            fail("Wallet was not stored");
            cursor.close();
        }
        List<String> storedWalletNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            storedWalletNames.add(cursor.getString(0));
        }
        return storedWalletNames;
    }

    private static String selectNameFrom(String table) {
        return String.format("SELECT name FROM %s", table);
    }

    static String storeWalletSql(Wallet wallet) {
        return String.format("INSERT INTO tbl_wallet VALUES ('%s', '%s');",
                wallet.uuid().toString(), wallet.name());
    }
}
