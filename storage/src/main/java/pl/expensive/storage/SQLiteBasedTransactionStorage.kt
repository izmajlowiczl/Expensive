package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor
import java.util.*

class SQLiteBasedTransactionStorage(private val database: Database) : TransactionStorage {

    override fun list(): List<Transaction> {
        val readableDatabase = database.readableDatabase
        val tables = "tbl_transaction t " +
                "LEFT JOIN tbl_currency AS cur ON t.currency=cur.code " +
                "LEFT JOIN tbl_category AS cat on t.category=cat.uuid "
        val columns = arrayOf(
                "t.uuid", "t.amount", "t.date", "t.description",
                "cur.code", "cur.format",
                "cat.uuid", "cat.name", "cat.color")
        val cursor = readableDatabase.query(tables, columns, null, null, null, null, null)

        val result = ArrayList<Transaction>()
        while (cursor.moveToNext()) {
            result.add(from(cursor))
        }

        cursor.close()
        return result
    }

    override fun insert(transaction: Transaction) {
        database.writableDatabase.insertOrThrow("tbl_transaction", null, toContentValues(transaction))
    }

    override fun update(transaction: Transaction) {
        database.writableDatabase.replace("tbl_transaction", null, toContentValues(transaction))
    }

    private fun toContentValues(transaction: Transaction): ContentValues {
        val cv = ContentValues()
        cv.put("uuid", transaction.uuid.toString())
        cv.put("amount", transaction.amount.toString())
        cv.put("currency", transaction.currency.code)
        cv.put("date", transaction.date)
        cv.put("description", transaction.description)
        cv.put("category", transaction.category?.uuid.toString())
        return cv
    }

    private fun from(cursor: Cursor): Transaction {
        val currency = Currency(cursor.getString(4), cursor.getString(5))
        val category = try {
            Category(cursor.uuid(6), cursor.getString(7), cursor.getString(8))
        } catch (ex: Exception) {
            null
        }

        return Transaction(
                cursor.uuid(0),
                cursor.bigDecimal(1),
                currency,
                cursor.getLong(2),
                cursor.getString(3),
                category)
    }
}
