package pl.expensive.wallet

import pl.expensive.calculateTotal
import pl.expensive.formatValue
import pl.expensive.storage.Currency
import pl.expensive.storage.Transaction
import java.util.*

data class WalletViewModel(val name: String,
                           val transactions: List<Transaction>,
                           val currency: Currency) {

    fun formattedTotal(locale: Locale = Locale.getDefault()): String {
        return currency.formatValue(locale, transactions.calculateTotal())
    }
}
