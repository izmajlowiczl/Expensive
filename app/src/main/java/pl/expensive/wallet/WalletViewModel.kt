package pl.expensive.wallet

import pl.expensive.formatValue
import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
import java.math.BigDecimal
import java.util.*

data class WalletViewModel(val name: String,
                           val transactions: List<Transaction>,
                           val currency: Currency) {

    fun formattedTotal(locale: Locale = Locale.getDefault()): String {
        return currency.formatValue(locale, calculateTotal())
    }

    /**
     * Calculate total of <b>absolute</b> amounts
     */
    private fun calculateTotal(): BigDecimal {
        var total = BigDecimal.ZERO
        transactions.map { total += it.amount.abs() }
        return total
    }

}
