package pl.expensive.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLiteBasedWalletsStorage implements WalletsStorage {
    private static final String TABLE = "tbl_wallet";
    private static final String COL_UUID = "uuid";
    private static final String COL_NAME = "name";
    private final Database database;

    public SQLiteBasedWalletsStorage(Database database) {
        this.database = database;
    }

    @NonNull
    @Override
    public Collection<Wallet> list() {
        SQLiteDatabase readableDatabase = database.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE, null, null, null, null, null, null);

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
            writableDatabase.insertOrThrow(TABLE, null, toContentValues(wallet));
        } catch (SQLiteConstraintException ex) {
            throw new IllegalStateException("Trying to store wallet, which already exist. Wallet -> " + wallet);
        }
    }

    private static Wallet fromCursor(Cursor cursor) {
        return Wallet.create(
                UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COL_UUID))),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
    }

    private static ContentValues toContentValues(Wallet wallet) {
        ContentValues cv = new ContentValues();
        cv.put(COL_UUID, wallet.uuid().toString());
        cv.put(COL_NAME, wallet.name());
        return cv;
    }
}
