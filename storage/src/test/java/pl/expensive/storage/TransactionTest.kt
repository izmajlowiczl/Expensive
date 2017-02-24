package pl.expensive.storage

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import pl.expensive.storage._Seeds.EUR
import java.math.BigDecimal

class TransactionTest {

    @Test
    fun createDeposit() {
        val deposit = Transaction.deposit(_Seeds.CASH_ID, BigDecimal("4.99"), EUR, "")

        assertThat(deposit.amount).isEqualTo(BigDecimal("4.99"))
    }

    @Test
    fun createWithdrawal() {
        val deposit = Transaction.withdrawal(_Seeds.CASH_ID, BigDecimal("4.99"), EUR, "")

        assertThat(deposit.amount).isEqualTo(BigDecimal("-4.99"))
    }

}
