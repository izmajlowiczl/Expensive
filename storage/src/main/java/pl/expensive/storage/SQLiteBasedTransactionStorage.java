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
        String[] columns = {"uuid", "wallet_uuid", "amount", "currency", "date", "description"};
        Cursor cursor = readableDatabase.query("tbl_transaction", columns, "wallet_uuid=?", new String[]{wallet.toString()}, null, null, null);

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
        cv.put("currency", transaction.currencyCode());
        cv.put("date", transaction.date());
        cv.put("description", transaction.description());
        return cv;
    }

    @NonNull
    private Transaction from(Cursor cursor) {
        return Transaction.create(
                UUID.fromString(cursor.getString(0)),
                UUID.fromString(cursor.getString(1)),
                new BigDecimal(cursor.getString(2)),
                cursor.getString(3),
                cursor.getLong(4),
                cursor.getString(5));
    }
}
