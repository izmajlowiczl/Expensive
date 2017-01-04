package pl.expensive.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

import java.util.UUID;

class Database extends SQLiteOpenHelper {
    static final Wallet DEFAULT_WALLET = Wallet.create(
            UUID.fromString("c2ee3260-94eb-4cc2-8d5c-af38f964fbd5"), "cash");

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
        createSchema(sqLiteDatabase);
        applySeeds(sqLiteDatabase);
    }

    private static void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tbl_wallet (uuid TEXT, name TEXT NOT NULL UNIQUE, PRIMARY KEY(uuid));");
    }

    private static void applySeeds(SQLiteDatabase db) {
        db.execSQL(String.format("INSERT INTO tbl_wallet VALUES('%s', '%s');", DEFAULT_WALLET.uuid(), DEFAULT_WALLET.name()));
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
