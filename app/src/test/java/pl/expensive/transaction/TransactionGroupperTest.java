package pl.expensive.transaction;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.threeten.bp.LocalDateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pl.expensive.storage.Transaction;

import static com.google.common.truth.Truth.assertThat;
import static pl.expensive.storage._Seeds.EUR;

public class TransactionGroupperTest {
    private static final LocalDateTime SOME_DAY = LocalDateTime.of(2001, 10, 19, 10, 20, 0);

    @Test
    public void empty() {
        assertThat(TransactionGrouper.group(new ArrayList<>())).isEmpty();
    }

    @Test
    public void singleTransaction() {
        List<Transaction> transactions = Arrays.asList(transactionAt(SOME_DAY));

        assertThat(TransactionGrouper.group(transactions))
                .containsExactly(SOME_DAY, transactions);
    }

    @Test
    public void transactionForTheSameDay() {
        List<Transaction> transactions = Arrays.asList(
                transactionAt(SOME_DAY),
                transactionAt(SOME_DAY));

        assertThat(TransactionGrouper.group(transactions))
                .containsExactly(SOME_DAY, transactions);
    }

    @Test
    public void transactionsForDifferentDays() {
        Transaction t1 = transactionAt(SOME_DAY);
        Transaction t2 = transactionAt(SOME_DAY.plusDays(1));
        List<Transaction> transactions = Arrays.asList(t1, t2);

        Map<LocalDateTime, List<Transaction>> group = TransactionGrouper.group(transactions);
        assertThat(group.size()).isEqualTo(2);
        assertThat(group.get(SOME_DAY)).containsExactly(t1);
        assertThat(group.get(SOME_DAY.plusDays(1))).containsExactly(t2);
    }

    @NonNull
    private Transaction transactionAt(LocalDateTime today) {
        return Transaction.create(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, EUR, today, "");
    }
}
