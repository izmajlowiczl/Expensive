package pl.expensive.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;

public class SQLiteBasedCurrencyStorage implements CurrencyStorage {
    private final Database database;

    public SQLiteBasedCurrencyStorage(Database database) {
        this.database = database;
    }

    @Override
    public Collection<Currency> list() {
        SQLiteDatabase readableDatabase = database.getReadableDatabase();
        Cursor cursor = readableDatabase.query("tbl_currency", new String[]{"code", "format"}, null, null, null, null, null);

        Collection<Currency> wallets = new ArrayList<>();
        while (cursor.moveToNext()) {
            wallets.add(Currency.create(cursor.getString(0), cursor.getString(1)));
        }

        cursor.close();
        return wallets;
    }

    @Override
    public void insert(Currency currency) {
        ContentValues cv = new ContentValues();
        cv.put("code", currency.code());
        cv.put("format", currency.format());
        try {
            database.getWritableDatabase().insertOrThrow("tbl_currency", null, cv);
        } catch (SQLiteConstraintException ex) {
            throw new IllegalStateException("Trying to store currency, which already exist. Currency -> " + currency);
        }

    }
}
