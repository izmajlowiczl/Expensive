package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import java.util.*

private const val alias_currency_code = "currency_code"
private const val alias_currency_format = "currency_format"

fun listTransactions(database: Database): List<Transaction> {
    val tables = "$tbl_transaction t LEFT JOIN $tbl_currency AS cur ON t.$tbl_transaction_col_currency=cur.$tbl_currency_col_code "
    val columns = arrayOf(
            "t.$tbl_transaction_col_id", "t.$tbl_transaction_col_amount", "t.$tbl_transaction_col_date", "t.$tbl_transaction_col_description",
            "cur.$tbl_currency_col_code AS $alias_currency_code", "cur.$tbl_currency_col_format AS $alias_currency_format")
    val cursor = database.readableDatabase.simpleQuery(tables, columns)
    val result = cursor.map { from(it) }
    cursor.close()
    return result
}

fun findTransaction(uuid: UUID, database: Database): Transaction? {
    val tables = "$tbl_transaction t LEFT JOIN $tbl_currency AS cur ON t.$tbl_transaction_col_currency=cur.$tbl_currency_col_code "
    val columns = arrayOf(
            "t.$tbl_transaction_col_id", "t.$tbl_transaction_col_amount", "t.$tbl_transaction_col_date", "t.$tbl_transaction_col_description",
            "cur.$tbl_currency_col_code AS $alias_currency_code", "cur.$tbl_currency_col_format AS $alias_currency_format")

    val cursor = database.readableDatabase.query(tables, columns, "$tbl_transaction_col_id=?", arrayOf(uuid.toString()), null, null, null)
    val result = cursor.map { from(it) }.firstOrNull()
    cursor.close()
    return result
}

fun insertTransaction(transaction: Transaction, database: Database) {
    try {
        database.writableDatabase.insertOrThrow(tbl_transaction, null, toContentValues(transaction))
    } catch (ex: SQLiteConstraintException) {
        throw IllegalStateException("!!!CONSTRAINT VIOLATION!!! Is Currency stored?")
    }
}

fun updateTransaction(transaction: Transaction, database: Database) {
    database.writableDatabase.replace(tbl_transaction, null, toContentValues(transaction))
}

private fun toContentValues(transaction: Transaction): ContentValues {
    val cv = ContentValues()
    cv.put(tbl_transaction_col_id, transaction.uuid.toString())
    cv.put(tbl_transaction_col_amount, transaction.amount.toString())
    cv.put(tbl_transaction_col_currency, transaction.currency.code)
    cv.put(tbl_transaction_col_date, transaction.date)
    cv.put(tbl_transaction_col_description, transaction.description)
    return cv
}

private fun from(cursor: Cursor): Transaction {
    return Transaction(
            cursor.uuid(tbl_transaction_col_id),
            cursor.bigDecimal(tbl_transaction_col_amount),
            Currency(cursor.string(alias_currency_code), cursor.string(alias_currency_format)),
            cursor.long(tbl_transaction_col_date),
            cursor.string(tbl_transaction_col_description))
}
