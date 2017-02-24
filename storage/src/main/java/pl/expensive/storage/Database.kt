package pl.expensive.storage

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.VisibleForTesting
import pl.expensive.storage._Seeds.CASH_ID
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN
import java.math.BigDecimal
import java.util.*

class Database : SQLiteOpenHelper {
    private var ctx: Context

    constructor(context: Context) : super(context, NAME, null, VERSION) {
        ctx = context
    }

    @VisibleForTesting
    constructor(context: Context, dbName: String?) : super(context, dbName, null, VERSION) {
        ctx = context
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        createSchema(sqLiteDatabase)
        applySeeds(sqLiteDatabase)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // not needed yet!
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    private fun createSchema(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE tbl_wallet (uuid TEXT NOT NULL, name TEXT NOT NULL UNIQUE, currency TEXT NOT NULL, PRIMARY KEY(uuid), FOREIGN KEY(currency) REFERENCES tbl_currency(code));")
        db.execSQL("CREATE TABLE tbl_currency (code TEXT NOT NULL, format TEXT NOT NULL, PRIMARY KEY(code));")
        db.execSQL("CREATE TABLE tbl_category (uuid TEXT NOT NULL, name TEXT NOT NULL, name_res TEXT, color TEXT, PRIMARY KEY(uuid));")
        db.execSQL("CREATE TABLE tbl_transaction (" +
                "uuid TEXT NOT NULL, amount TEXT NOT NULL, " +
                "currency TEXT NOT NULL, " +
                "date INTEGER NOT NULL, " +
                "description TEXT, " +
                "wallet_uuid TEXT NOT NULL, " +
                "category TEXT, " +
                "PRIMARY KEY(uuid), " +
                "FOREIGN KEY(wallet_uuid) REFERENCES tbl_wallet(uuid), " +
                "FOREIGN KEY(currency) REFERENCES tbl_currency(code), " +
                "FOREIGN KEY(category) REFERENCES tbl_category(uuid));")
    }

    private fun applySeeds(db: SQLiteDatabase) {
        // Cash wallet
        db.execSQL(String.format("INSERT INTO tbl_wallet VALUES('%s', '%s', '%s');", CASH_ID, ctx.getString(R.string.wallet_name), ctx.getString(R.string.wallet_currency)))

        // Currencies
        storeCurrency(db, EUR)
        storeCurrency(db, GBP)
        storeCurrency(db, CHF)
        storeCurrency(db, PLN)
        storeCurrency(db, CZK)

        // Categories
        db.storeCategory(_Seeds.OTHER)
        db.storeCategory(_Seeds.GROCERY)
        db.storeCategory(_Seeds.FOOD)
        db.storeCategory(_Seeds.TRAVEL)
        db.storeCategory(_Seeds.TRANSPORT)
        db.storeCategory(_Seeds.CAR)
        db.storeCategory(_Seeds.SPORT)
        db.storeCategory(_Seeds.HEALTH)
        db.storeCategory(_Seeds.CLOTHES)
        db.storeCategory(_Seeds.HOUSE)
        db.storeCategory(_Seeds.BILLS)
    }

    private fun storeCurrency(db: SQLiteDatabase, currency: Currency) {
        db.execSQL(String.format("INSERT INTO tbl_currency VALUES('%s', '%s');", currency.code, currency.format))
    }

    private fun SQLiteDatabase.storeCategory(category: Category) {
        execSQL(String.format("INSERT INTO tbl_category VALUES('%s', '%s', '%s', '%s');", category.uuid, category.name, category.name_res, category.color))
    }

    companion object {
        private val VERSION = 1
        private val NAME = "expensive.db"
    }
}

fun Cursor.getUUID(columnIndex: Int): UUID {
    return UUID.fromString(getString(columnIndex))
}

fun Cursor.getBigDecimal(columnIndex: Int): BigDecimal {
    return BigDecimal(getString(columnIndex))
}
