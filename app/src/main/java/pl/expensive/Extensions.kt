package pl.expensive

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
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

fun View.expandDown(): Animation {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    visibility = View.VISIBLE
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            layoutParams.height = if (interpolatedTime == 1f) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                (targetHeight * interpolatedTime).toInt()
            }
            requestLayout()
        }

        override fun willChangeBounds(): Boolean = true
    }
    animation

    // 1dp/ms
    animation.duration = (targetHeight / context.resources.displayMetrics.density).toInt().toLong()
    return animation
}

fun View.collapseUp(): Animation {
    val initialHeight = measuredHeight

    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                visibility = GONE
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean = true

    }

    // 1dp/ms
    animation.duration = (initialHeight / context.resources.displayMetrics.density).toInt().toLong()
    return animation
}

fun Currency.formatValue(locale: Locale = Locale.getDefault(), money: BigDecimal): String {
    val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
    numberFormat.applyPattern(format)
    return numberFormat.format(money)
}

