package pl.expensive.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.VisibleForTesting
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN

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

    private val supportedLanguages by lazy { listOf("pl", "de") }

    private fun createSchema(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE tbl_currency (code TEXT NOT NULL, format TEXT NOT NULL, PRIMARY KEY(code));")
        db.execSQL("CREATE TABLE tbl_transaction (" +
                "uuid TEXT NOT NULL, " +
                "amount TEXT NOT NULL, " +
                "currency TEXT NOT NULL, " +
                "date INTEGER NOT NULL, " +
                "description TEXT, " +
                "category TEXT, " +
                "PRIMARY KEY(uuid), " +
                "FOREIGN KEY(currency) REFERENCES tbl_currency(code), " +
                "FOREIGN KEY(category) REFERENCES tbl_category(uuid));")

        // Categories
        db.createCategoryTablesForLanguages(supportedLanguages)
    }

    private fun SQLiteDatabase.createCategoryTablesForLanguages(supportedLanguages: List<String>) {
        // Default
        execSQL("CREATE TABLE tbl_category (uuid TEXT NOT NULL, name TEXT NOT NULL, color TEXT, PRIMARY KEY(uuid));")

        // Localised
        supportedLanguages.forEach { name ->
            execSQL("CREATE TABLE tbl_category_$name (uuid TEXT NOT NULL, name TEXT NOT NULL, color TEXT, PRIMARY KEY(uuid));")
        }
    }

    private fun applySeeds(db: SQLiteDatabase) {
        // Currencies
        storeCurrency(db, EUR)
        storeCurrency(db, GBP)
        storeCurrency(db, CHF)
        storeCurrency(db, PLN)
        storeCurrency(db, CZK)

        // Categories
        try {
            db.beginTransaction()

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

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun storeCurrency(db: SQLiteDatabase, currency: Currency) {
        db.execSQL(String.format("INSERT INTO tbl_currency VALUES('%s', '%s');", currency.code, currency.format))
    }

    private fun SQLiteDatabase.storeCategory(category: Category) {
        execSQL(String.format("INSERT INTO tbl_category VALUES('%s', '%s', '%s');", category.uuid, category.name, category.color))
    }

    companion object {
        private val VERSION = 1
        private val NAME = "expensive.db"
    }
}
