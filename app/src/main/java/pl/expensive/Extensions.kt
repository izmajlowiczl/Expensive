package pl.expensive

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import pl.expensive.storage.Currency
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun ViewGroup.inflateLayout(@LayoutRes layout: Int): View =
        LayoutInflater.from(context).inflate(layout, this, false)

fun Currency.formatValue(locale: Locale = Locale.getDefault(), money: BigDecimal): String {
    val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
    numberFormat.applyPattern(format)
    return numberFormat.format(money)
}
