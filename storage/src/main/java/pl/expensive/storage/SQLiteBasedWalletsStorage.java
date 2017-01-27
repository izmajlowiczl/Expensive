package pl.expensive.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

class SQLiteBasedWalletsStorage implements WalletsStorage {
    private final Database database;

    SQLiteBasedWalletsStorage(Database database) {
        this.database = database;
    }

    @NonNull
    @Override
    public Collection<Wallet> list() {
        SQLiteDatabase readableDatabase = database.getReadableDatabase();
        String[] columns = {"w.uuid", "w.name", "c.code ", "c.format"};
        Cursor cursor = readableDatabase.query("tbl_wallet w, " + " tbl_currency c", columns, "w.currency=c.code", null, null, null, null);

        Collection<Wallet> wallets = new ArrayList<>();
        while (cursor.moveToNext()) {
            wallets.add(fromCursor(cursor));
        }

        cursor.close();
        return wallets;
    }

    @Override
    public void insert(@NonNull Wallet wallet) {
        SQLiteDatabase writableDatabase = database.getWritableDatabase();
        try {
            writableDatabase.insertOrThrow("tbl_wallet", null, toContentValues(wallet));
        } catch (SQLiteConstraintException ex) {
            throw new IllegalStateException("Trying to store wallet, which already exist. Wallet -> " + wallet);
        }
    }

    private static Wallet fromCursor(Cursor cursor) {
        Currency currency = Currency.create(cursor.getString(2), cursor.getString(3));
        return Wallet.create(
                UUID.fromString(cursor.getString(0)),
                cursor.getString(1),
                currency);
    }

    private static ContentValues toContentValues(Wallet wallet) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", wallet.uuid().toString());
        cv.put("name", wallet.name());
        cv.put("currency", wallet.currency().code());
        return cv;
    }
}
