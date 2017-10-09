package pl.expensive.storage

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.VisibleForTesting
import pl.expensive.storage.SQLiteTableBuilder.newTable
import java.math.BigDecimal
import java.util.*

private const val DB_VERSION = 1
private const val DB_NAME = "expensive.db"

class Database : SQLiteOpenHelper {
    private var ctx: Context
    private var currencyRepository: CurrencyRepository

    constructor(context: Context, currencyRepository: CurrencyRepository) : super(context, DB_NAME, null, DB_VERSION) {
        ctx = context
        this.currencyRepository = currencyRepository
    }

    @VisibleForTesting
    constructor(context: Context, currencyRepository: CurrencyRepository, dbName: String?) : super(context, dbName, null, DB_VERSION) {
        ctx = context
        this.currencyRepository = currencyRepository
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        createSchema(sqLiteDatabase)
        applySeeds(sqLiteDatabase)
        currencyRepository.setDefaultCurrency(_Seeds.PLN)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // not needed yet!
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    private fun createSchema(db: SQLiteDatabase) {
        db.execSQL(newTable(tbl_currency)
                .withMandatoryText(tbl_currency_col_code)
                .withMandatoryText(tbl_currency_col_format)
                .withPrimaryKey(tbl_currency_col_code)
                .build())

        db.execSQL(newTable(tbl_transaction)
                .withMandatoryText(tbl_transaction_col_id)
                .withMandatoryText(tbl_transaction_col_amount)
                .withMandatoryText(tbl_transaction_col_currency)
                .withMandatoryDecimal(tbl_transaction_col_date)
                .withOptionalText(tbl_transaction_col_description)
                .withPrimaryKey(tbl_transaction_col_id)
                .withForeignKey(tbl_transaction_col_currency, tbl_currency, tbl_currency_col_code)
                .build())
    }
}

//region Helpers
/**
 * Maps Cursor to Object with given transformation
 */
inline fun <T> Cursor.map(transform: (Cursor) -> T): List<T> {
    val result = ArrayList<T>()
    while (moveToNext()) {
        result.add(transform(this))
    }
    return result
}

/**
 * Queries given table for columns.
 * Do not forget to close returned Cursor object.
 */
fun SQLiteDatabase.simpleQuery(table: String, columns: Array<String>): Cursor =
        query(table, columns, null, null, null, null, null)

fun Cursor.string(columnName: String): String = getString(getColumnIndex(columnName))
fun Cursor.long(columnName: String): Long = getLong(getColumnIndex(columnName))
fun Cursor.uuid(columnName: String): UUID = UUID.fromString(string(columnName))
fun String.toUUID(): UUID = UUID.fromString(this)

fun Cursor.bigDecimal(columnName: String) = BigDecimal(string(columnName))
fun String.asBigDecimal(): BigDecimal = BigDecimal(this)
//endregion

