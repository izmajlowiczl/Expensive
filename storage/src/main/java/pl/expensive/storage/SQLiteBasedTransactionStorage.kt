package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import java.math.BigDecimal
import java.util.*

class SQLiteBasedTransactionStorage(private val database: Database) : TransactionStorage {

    override fun select(wallet: UUID): List<Transaction> {
        val readableDatabase = database.readableDatabase
        val columns = arrayOf("t.uuid", "t.wallet_uuid", "t.amount", "t.currency", "t.date", "t.description", "c.format")
        val cursor = readableDatabase.query("tbl_transaction t , " + " tbl_currency c", columns, "wallet_uuid=? AND t.currency=c.code", arrayOf(wallet.toString()), null, null, null)

        val result = ArrayList<Transaction>()
        while (cursor.moveToNext()) {
            result.add(from(cursor))
        }

        cursor.close()
        return result
    }

    override fun list(): List<Transaction> {
        throw UnsupportedOperationException("Cannot perform list on transactions. Use select(wallet)!")
    }

    override fun insert(transaction: Transaction) {
        try {
            database.writableDatabase.insertOrThrow("tbl_transaction", null, toContentValues(transaction))
        } catch (ex: SQLiteConstraintException) {
            throw IllegalStateException("No wallet for transaction?")
        }

    }

    private fun toContentValues(transaction: Transaction): ContentValues {
        val cv = ContentValues()
        cv.put("uuid", transaction.uuid.toString())
        cv.put("wallet_uuid", transaction.wallet.toString())
        cv.put("amount", transaction.amount.toString())
        cv.put("currency", transaction.currency.code)
        cv.put("date", transaction.date)
        cv.put("description", transaction.description)
        cv.put("category", transaction.category?.name)
        return cv
    }

    private fun from(cursor: Cursor): Transaction {
        val currency = Currency(cursor.getString(3), cursor.getString(6))
        return Transaction.create(
                UUID.fromString(cursor.getString(0)),
                UUID.fromString(cursor.getString(1)),
                BigDecimal(cursor.getString(2)),
                currency,
                cursor.getLong(4),
                cursor.getString(5))
    }
}
