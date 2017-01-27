package pl.expensive.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.VisibleForTesting

import pl.expensive.storage._Seeds.CASH
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN

class Database : SQLiteOpenHelper {

    constructor(context: Context) : super(context, NAME, null, VERSION)

    @VisibleForTesting
    constructor(context: Context, dbName: String?) : super(context, dbName, null, VERSION)

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
        db.execSQL("CREATE TABLE tbl_transaction (uuid TEXT NOT NULL, amount TEXT NOT NULL, currency TEXT NOT NULL, date INTEGER NOT NULL, description TEXT, wallet_uuid TEXT NOT NULL, PRIMARY KEY(uuid), FOREIGN KEY(wallet_uuid) REFERENCES tbl_wallet(uuid), FOREIGN KEY(currency) REFERENCES tbl_currency(code));")
    }

    private fun applySeeds(db: SQLiteDatabase) {
        // Cash wallet
        db.execSQL(String.format("INSERT INTO tbl_wallet VALUES('%s', '%s', '%s');", CASH.uuid, CASH.name, EUR.code))

        // Currencies
        storeCurrency(db, EUR)
        storeCurrency(db, GBP)
        storeCurrency(db, CHF)
        storeCurrency(db, PLN)
        storeCurrency(db, CZK)
    }

    private fun storeCurrency(db: SQLiteDatabase, currency: Currency) {
        db.execSQL(String.format("INSERT INTO tbl_currency VALUES('%s', '%s');", currency.code, currency.format))
    }

    companion object {
        private val VERSION = 1
        private val NAME = "expensive.db"
    }
}
