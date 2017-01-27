package pl.expensive.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

import static pl.expensive.storage._Seeds.CASH;
import static pl.expensive.storage._Seeds.CHF;
import static pl.expensive.storage._Seeds.CZK;
import static pl.expensive.storage._Seeds.EUR;
import static pl.expensive.storage._Seeds.GBP;
import static pl.expensive.storage._Seeds.PLN;

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
        createSchema(sqLiteDatabase);
        applySeeds(sqLiteDatabase);
    }

    private static void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tbl_wallet (uuid TEXT NOT NULL, name TEXT NOT NULL UNIQUE, currency TEXT NOT NULL, PRIMARY KEY(uuid), FOREIGN KEY(currency) REFERENCES tbl_currency(code));");
        db.execSQL("CREATE TABLE tbl_currency (code TEXT NOT NULL, format TEXT NOT NULL, PRIMARY KEY(code));");
        db.execSQL("CREATE TABLE tbl_transaction (uuid TEXT NOT NULL, amount TEXT NOT NULL, currency TEXT NOT NULL, date INTEGER NOT NULL, description TEXT, wallet_uuid TEXT NOT NULL, PRIMARY KEY(uuid), FOREIGN KEY(wallet_uuid) REFERENCES tbl_wallet(uuid), FOREIGN KEY(currency) REFERENCES tbl_currency(code));");
    }

    private static void applySeeds(SQLiteDatabase db) {
        // Cash wallet
        db.execSQL(String.format("INSERT INTO tbl_wallet VALUES('%s', '%s', '%s');", CASH.uuid(), CASH.name(), EUR.code()));

        // Currencies
        storeCurrency(db, EUR);
        storeCurrency(db, GBP);
        storeCurrency(db, CHF);
        storeCurrency(db, PLN);
        storeCurrency(db, CZK);
    }

    private static void storeCurrency(SQLiteDatabase db, Currency currency) {
        db.execSQL(String.format("INSERT INTO tbl_currency VALUES('%s', '%s');", currency.code(), currency.format()));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // not needed yet!
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}
