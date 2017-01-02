package pl.expensive.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

class Database extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "expensive.db";

    Database(Context context) {
        super(context, NAME, null, VERSION);
    }

    @VisibleForTesting
    Database(Context context, String dbName) {
        super(context, dbName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE tbl_wallet (uuid TEXT, name TEXT NOT NULL UNIQUE, PRIMARY KEY(uuid));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // not needed yet!
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable Foreign keys to be able to delete cascade
        // Fixme: 01.01.2017 Why is it for read only db?
        if (db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
