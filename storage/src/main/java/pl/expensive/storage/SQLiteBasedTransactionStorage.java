package pl.expensive.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLiteBasedTransactionStorage implements TransactionStorage {
    private final Database database;

    public SQLiteBasedTransactionStorage(Database database) {
        this.database = database;
    }

    @Override
    public Collection<Transaction> select(UUID wallet) {
        SQLiteDatabase readableDatabase = database.getReadableDatabase();
        String[] columns = {"t.uuid", "t.wallet_uuid", "t.amount", "t.currency", "t.date", "t.description", "c.format"};
        Cursor cursor = readableDatabase.query("tbl_transaction t , " + " tbl_currency c", columns, "wallet_uuid=? AND t.currency=c.code", new String[]{wallet.toString()}, null, null, null);

        Collection<Transaction> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(from(cursor));
        }

        cursor.close();
        return result;
    }

    @Override
    public Collection<Transaction> list() {
        throw new UnsupportedOperationException("Cannot perform list on transactions. Use select(wallet)!");
    }

    @Override
    public void insert(Transaction transaction) {
        try {
            database.getWritableDatabase().insertOrThrow("tbl_transaction", null, toContentValues(transaction));
        } catch (SQLiteConstraintException ex) {
            throw new IllegalStateException("No wallet for transaction?");
        }
    }

    @NonNull
    private ContentValues toContentValues(Transaction transaction) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", transaction.uuid().toString());
        cv.put("wallet_uuid", transaction.wallet().toString());
        cv.put("amount", transaction.amount().toString());
        cv.put("currency", transaction.currency().code());
        cv.put("date", transaction.date());
        cv.put("description", transaction.description());
        return cv;
    }

    @NonNull
    private Transaction from(Cursor cursor) {
        Currency currency = Currency.create(cursor.getString(3), cursor.getString(6));
        return Transaction.create(
                UUID.fromString(cursor.getString(0)),
                UUID.fromString(cursor.getString(1)),
                new BigDecimal(cursor.getString(2)),
                currency,
                cursor.getLong(4),
                cursor.getString(5));
    }
}
