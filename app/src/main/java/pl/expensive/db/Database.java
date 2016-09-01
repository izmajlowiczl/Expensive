package pl.expensive.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

public class Database extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "expensive.db";

    public Database(Context context) {
        super(context, NAME, null, VERSION);
    }

    @VisibleForTesting
    Database(Context context, String dbName) {
        super(context, dbName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE tbl_wallet (uuid TEXT, name TEXT NOT NULL UNIQUE, PRIMARY KEY(uuid));");
//        sqLiteDatabase.execSQL("CREATE TABLE tbl_transaction (uuid TEXT, name TEXT NOT NULL, value TEXT NOT NULL, currency TEXT NOT NULL, start INTEGER NOT NULL, wallet TEXT, PRIMARY KEY(uuid), FOREIGN KEY(wallet) REFERENCES tbl_wallet(uuid) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // not needed yet!
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable Foreign keys to be able to delete cascade
        if (db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

//    fun insertTransaction(transaction: Transaction) {
//        try {
//            writableDatabase.insertOrThrow("tbl_transaction", null, transaction.toContentValues())
//        } catch (sqlEx: SQLException) {
//            if (sqlEx is SQLiteConstraintException) {
//                throw IllegalStateException("Inserting transaction [" + transaction + "] for not existing wallet [" + transaction.wallet.toString() + "]")
//            }
//        }
//    }
//
//    fun listTransactions(wallet:UUID): Collection<Transaction>
//
//    {
//        val cursor = readableDatabase.query("tbl_transaction", null, "wallet=?", arrayOf(wallet.toString()), null, null, null)
//        val transactions = ArrayList<Transaction>()
//        while (cursor.moveToNext()) {
//            transactions.add(fromCursor(cursor, wallet))
//        }
//        cursor.close()
//        return transactions
//    }
//
//    private fun fromCursor(cursor:Cursor, wallet: UUID): Transaction {
//        return Transaction(
//                UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow("uuid"))),
//                cursor.getString(cursor.getColumnIndexOrThrow("name")),
//                Money(
//                        BigDecimal(cursor.getString(cursor.getColumnIndexOrThrow("value"))),
//                        Locale.getDefault() // Fixme: Restore locale from stored value
//                ),
//                Date(cursor.getLong(cursor.getColumnIndexOrThrow("start"))),
//                wallet)
//    }
}
