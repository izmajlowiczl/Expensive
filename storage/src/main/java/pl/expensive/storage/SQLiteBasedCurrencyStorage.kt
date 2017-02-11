package pl.expensive.storage

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import java.util.*

class SQLiteBasedCurrencyStorage(val database: Database) : CurrencyStorage {

    override fun list(): List<Currency> {
        val readableDatabase = database.readableDatabase
        val cursor = readableDatabase.query("tbl_currency", arrayOf("code", "format"), null, null, null, null, null)

        val result = ArrayList<Currency>()
        while (cursor.moveToNext()) {
            result.add(Currency(cursor.getString(0), cursor.getString(1)))
        }

        cursor.close()
        return result
    }

    override fun insert(currency: Currency) {
        val cv = ContentValues()
        cv.put("code", currency.code)
        cv.put("format", currency.format)

        try {
            database.writableDatabase.insertOrThrow("tbl_currency", null, cv)
        } catch (ex: SQLiteConstraintException) {
            throw IllegalStateException("Trying to store currency, which already exist. Currency -> " + currency)
        }
    }
}
