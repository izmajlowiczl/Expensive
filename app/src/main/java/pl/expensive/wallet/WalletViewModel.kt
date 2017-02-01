package pl.expensive.wallet

import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

data class WalletViewModel(val name: String,
                           val transactions: List<Transaction>,
                           val currency: Currency) {

    fun formattedTotal(locale: Locale = Locale.getDefault()): String {
        return formatValue(locale, calculateTotal(), currency)
    }

    private fun calculateTotal(): BigDecimal {
        var total = BigDecimal.ZERO
        transactions.map { total += it.amount }
        return total
    }

    private fun formatValue(locale: Locale, money: BigDecimal, currency: Currency): String {
        val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
        numberFormat.applyPattern(currency.format)
        return numberFormat.format(money)
    }
}
