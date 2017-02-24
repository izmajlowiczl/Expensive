package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import java.math.BigDecimal
import java.util.*

class SQLiteBasedTransactionStorage(private val database: Database) : TransactionStorage {
    override fun select(wallet: UUID): List<Transaction> {
        val readableDatabase = database.readableDatabase
        val tables = "tbl_transaction t " +
                "LEFT JOIN tbl_currency AS cur ON t.currency=cur.code " +
                "LEFT JOIN tbl_category AS cat on t.category=cat.name "
        val columns = arrayOf(
                "t.uuid", "t.wallet_uuid", "t.amount", "t.date", "t.description",
                "cur.code", "cur.format",
                "cat.uuid", "cat.name", "cat.name_res", "cat.color")
        val cursor = readableDatabase.query(tables, columns, "t.wallet_uuid=?", arrayOf(wallet.toString()), null, null, null)

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

    override fun update(transaction: Transaction) {
        database.writableDatabase.replace("tbl_transaction", null, toContentValues(transaction))
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
        val currency = Currency(cursor.getString(5), cursor.getString(6))
        val category = try {
            Category(cursor.getUUID(7), cursor.getString(8), cursor.getString(9), cursor.getString(10))
        } catch (ex: Exception) {
            null
        }

        return Transaction(
                UUID.fromString(cursor.getString(0)),
                UUID.fromString(cursor.getString(1)),
                BigDecimal(cursor.getString(2)),
                currency,
                cursor.getLong(3),
                cursor.getString(4),
                category)
    }
}
