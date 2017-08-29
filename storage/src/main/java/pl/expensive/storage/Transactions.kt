package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import java.util.*

fun listTransactions(database: Database): List<Transaction> {
    val tables = "tbl_transaction t LEFT JOIN tbl_currency AS cur ON t.currency=cur.code "
    val columns = arrayOf(
            "t.uuid", "t.amount", "t.date", "t.description",
            "cur.code", "cur.format")
    val cursor = database.readableDatabase.simpleQuery(tables, columns)
    val result = cursor.map { from(it) }
    cursor.close()
    return result
}

fun insertTransaction(transaction: Transaction, database: Database) {
    try {
        database.writableDatabase.insertOrThrow("tbl_transaction", null, toContentValues(transaction))
    } catch (ex: SQLiteConstraintException) {
        throw IllegalStateException("!!!CONSTRAINT VIOLATION!!! Is Currency stored?")
    }
}

fun updateTransaction(transaction: Transaction, database: Database) {
    database.writableDatabase.replace("tbl_transaction", null, toContentValues(transaction))
}

fun findTransaction(uuid: UUID, database: Database): Transaction? {
    val tables = "tbl_transaction t LEFT JOIN tbl_currency AS cur ON t.currency=cur.code "
    val columns = arrayOf(
            "t.uuid", "t.amount", "t.date", "t.description",
            "cur.code", "cur.format")

    val cursor = database.readableDatabase.query(tables, columns, "uuid=?", arrayOf(uuid.toString()), null, null, null)
    val result = cursor.map { from(it) }.firstOrNull()
    cursor.close()
    return result
}

private fun toContentValues(transaction: Transaction): ContentValues {
    val cv = ContentValues()
    cv.put("uuid", transaction.uuid.toString())
    cv.put("amount", transaction.amount.toString())
    cv.put("currency", transaction.currency.code)
    cv.put("date", transaction.date)
    cv.put("description", transaction.description)
    return cv
}

private fun from(cursor: Cursor): Transaction {
    val currency = Currency(cursor.getString(4), cursor.getString(5))
    return Transaction(
            cursor.uuid(0),
            cursor.bigDecimal(1),
            currency,
            cursor.getLong(2),
            cursor.getString(3))
}
