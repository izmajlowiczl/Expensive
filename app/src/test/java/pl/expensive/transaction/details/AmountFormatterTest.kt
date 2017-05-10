package pl.expensive.transaction.details

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import pl.expensive.formatValue
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN
import java.math.BigDecimal
import java.util.*

class AmountFormatterTest {
    @Test
    fun formatTotalForSupportedCurrencies() {
        mapOf("£9,99" to GBP,
                "9,99 €" to EUR,
                "9,99 CHF" to CHF,
                "9,99 zł" to PLN,
                "9,99 Kč" to CZK)
                .forEach {
                    assertThat(it.key).isEqualTo(
                            it.value.formatValue(
                                    money = BigDecimal("9.99"),
                                    locale = Locale.GERMANY
                            ))
                }
    }
}
