package pl.expensive.storage

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.VisibleForTesting
import java.math.BigDecimal
import java.util.*

private const val DB_VERSION = 1
private const val DB_NAME = "expensive.db"

class Database : SQLiteOpenHelper {
    private var ctx: Context
    private var prefs: SharedPreferences

    constructor(context: Context, preferences: SharedPreferences) : super(context, DB_NAME, null, DB_VERSION) {
        ctx = context
        prefs = preferences
    }

    @VisibleForTesting
    constructor(context: Context, preferences: SharedPreferences, dbName: String?) : super(context, dbName, null, DB_VERSION) {
        ctx = context
        prefs = preferences
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        createSchema(sqLiteDatabase)
        applySeeds(sqLiteDatabase)
        setDefaultCurrency(_Seeds.PLN, prefs)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // not needed yet!
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    private fun createSchema(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $tbl_currency (
                                    $tbl_currency_col_code TEXT NOT NULL,
                                    $tbl_currency_col_format TEXT NOT NULL,
                                    PRIMARY KEY($tbl_currency_col_code));""")

        db.execSQL("""CREATE TABLE $tbl_transaction (
                $tbl_transaction_col_id TEXT NOT NULL,
                $tbl_transaction_col_amount TEXT NOT NULL,
                $tbl_transaction_col_currency TEXT NOT NULL,
                $tbl_transaction_col_date INTEGER NOT NULL,
                $tbl_transaction_col_description TEXT,
                PRIMARY KEY($tbl_transaction_col_id),
                FOREIGN KEY($tbl_transaction_col_currency) REFERENCES $tbl_currency($tbl_currency_col_code));""")
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

