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
        db.execSQL("""CREATE TABLE tbl_currency (
                                    code TEXT NOT NULL,
                                    format TEXT NOT NULL,
                                    PRIMARY KEY(code));""")

        db.execSQL("""CREATE TABLE tbl_transaction (
                uuid TEXT NOT NULL,
                amount TEXT NOT NULL,
                currency TEXT NOT NULL,
                date INTEGER NOT NULL,
                description TEXT,
                PRIMARY KEY(uuid),
                FOREIGN KEY(currency) REFERENCES tbl_currency(code));""")
    }
}

fun String.toUUID(): UUID = UUID.fromString(this)
fun String.asBigDecimal(): BigDecimal = BigDecimal(this)

fun Cursor.uuid(columnIndex: Int): UUID = UUID.fromString(getString(columnIndex))
fun Cursor.bigDecimal(columnIndex: Int) = BigDecimal(getString(columnIndex))
