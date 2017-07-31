package pl.expensive.storage

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.sqlite.SQLiteConstraintException
import pl.expensive.storage._Seeds.EUR
import java.util.*

private const val KEY_CODE: String = "DEFAULT_CURRENCY_CODE"
private const val KEY_FORMAT: String = "DEFAULT_CURRENCY_FORMAT"

fun setDefaultCurrency(currency: Currency, preferences: SharedPreferences) =
        preferences.edit()
                .putString(KEY_CODE, currency.code)
                .putString(KEY_FORMAT, currency.format)
                .apply()

fun getDefaultCurrency(preferences: SharedPreferences, fallbackCurrency: Currency = EUR) = Currency(
        code = preferences.getString(KEY_CODE, fallbackCurrency.code),
        format = preferences.getString(KEY_FORMAT, fallbackCurrency.format))

fun listCurrencies(database: Database): List<Currency> {
    val readableDatabase = database.readableDatabase
    val cursor = readableDatabase.query("tbl_currency", arrayOf("code", "format"), null, null, null, null, null)

    val result = ArrayList<Currency>()
    while (cursor.moveToNext()) {
        result.add(Currency(cursor.getString(0), cursor.getString(1)))
    }

    cursor.close()
    return result
}

fun insertCurrency(currency: Currency, database: Database) {
    val cv = ContentValues()
    cv.put("code", currency.code)
    cv.put("format", currency.format)

    try {
        database.writableDatabase.insertOrThrow("tbl_currency", null, cv)
    } catch (ex: SQLiteConstraintException) {
        throw IllegalStateException("Trying to store currency, which already exist. Currency -> " + currency)
    }
}
