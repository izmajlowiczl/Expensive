package pl.expensive.storage

import android.content.SharedPreferences
import pl.expensive.storage._Seeds.EUR

private const val KEY_CODE: String = "DEFAULT_CURRENCY_CODE"
private const val KEY_FORMAT: String = "DEFAULT_CURRENCY_FORMAT"

fun setDefaultCurrency(currency: Currency, preferences: SharedPreferences) {
    preferences.edit()
            .putString(KEY_CODE, currency.code)
            .putString(KEY_FORMAT, currency.format)
            .apply()
}

fun getDefaultCurrency(preferences: SharedPreferences): Currency {
    return Currency(code = preferences.getString(KEY_CODE, EUR.code),
            format = preferences.getString(KEY_FORMAT, EUR.format))
}

