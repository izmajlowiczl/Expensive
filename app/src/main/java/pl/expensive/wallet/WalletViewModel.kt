package pl.expensive.wallet

import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

data class WalletViewModel(val name: String,
                           val transactions: List<Transaction>,
                           val currency: Currency) {

    fun formattedTotal(): String {
        return formatValue(Locale.getDefault(), calculateTotal(), currency)
    }

    fun calculateTotal(): BigDecimal {
        var total = BigDecimal.ZERO
        transactions.map { total += it.amount }
        return total
    }

    fun formatValue(locale: Locale, money: BigDecimal, currency: Currency): String {
        val numberFormat = DecimalFormat.getInstance(locale) as DecimalFormat
        numberFormat.applyPattern(currency.format)
        return numberFormat.format(money)
    }
}
