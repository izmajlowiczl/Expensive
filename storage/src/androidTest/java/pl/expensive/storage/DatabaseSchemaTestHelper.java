package pl.expensive.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
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


    // Currencies

    public static void assertCurrencyCodeStored(SQLiteDatabase db, String code) {
        Cursor cursor = db.rawQuery(queryForCurrencyCode(code), null);
        if (cursor == null || !cursor.moveToNext()) {
            Assert.fail("Cannot find currency code");
        }

        assertThat(cursor.getString(0))
                .isEqualTo(code);
    }

    public static String queryForCurrencyCode(String code) {
        return String.format("SELECT code FROM tbl_currency WHERE code='%s';", code);
    }

    // -- Currencies
}
