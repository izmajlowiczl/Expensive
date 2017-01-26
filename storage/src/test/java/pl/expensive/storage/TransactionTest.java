package pl.expensive.storage;

import org.junit.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static pl.expensive.storage._Seeds.CASH;
import static pl.expensive.storage._Seeds.EUR;

public class TransactionTest {

    @Test
    public void createDeposit() {
        Transaction deposit = Transaction.deposit(CASH.uuid(), new BigDecimal("4.99"), EUR, "");

        assertThat(deposit.amount()).isEqualTo(new BigDecimal("4.99"));
    }

    @Test
    public void createWithdrawal() {
        Transaction deposit = Transaction.withdrawal(CASH.uuid(), new BigDecimal("4.99"), EUR, "");

        assertThat(deposit.amount()).isEqualTo(new BigDecimal("-4.99"));
    }

}
