package pl.expensive.wallet

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import pl.expensive.storage.Transaction
import pl.expensive.storage._Seeds.CASH
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN
import java.math.BigDecimal
import java.util.*

class WalletViewModelTest {
    @Test
    fun formatTotalForSupportedCurrencies() {
        mapOf("£9,99" to GBP,
                "9,99 €" to EUR,
                "9,99 CHF" to CHF,
                "9,99 zł" to PLN,
                "9,99 Kč" to CZK)
                .forEach {
                    val viewModel = WalletViewModel(
                            CASH.name,
                            listOf(Transaction.deposit(CASH.uuid, BigDecimal("9.99"), it.value, "")),
                            it.value)

                    assertThat(it.key).isEqualTo(viewModel.formattedTotal(Locale.GERMANY))
                }
    }
}
