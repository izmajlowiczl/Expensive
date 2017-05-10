package pl.expensive

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.LayoutRes
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import org.threeten.bp.format.DateTimeFormatter
import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
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

fun View.show(value: Boolean) {
    visibility = if (value) VISIBLE else GONE
}

fun EditText.afterTextChanged(action: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            action()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun ImageView.tint(color: Int) {
    setColorFilter(ResourcesCompat.getColor(context.resources, color, context.theme), PorterDuff.Mode.SRC_IN)
}

/**
 * Calculate total of <b>absolute</b> amounts
 */
fun List<Transaction>.calculateTotal(): BigDecimal {
    var total = BigDecimal.ZERO
    map { total += it.amount }
    return total
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

val dateTimeFormat = DateTimeFormatter.ofPattern("MMM d")
