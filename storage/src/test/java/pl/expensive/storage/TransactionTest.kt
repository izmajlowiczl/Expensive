package pl.expensive.storage

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import pl.expensive.storage._Seeds.EUR
import java.math.BigDecimal

class TransactionTest {
    @Test
    fun createWithdrawal() {
        val deposit = withdrawal(
                amount = BigDecimal("4.99"),
                currency = EUR)

        assertThat(deposit.amount).isEqualTo(BigDecimal("-4.99"))
    }
}
