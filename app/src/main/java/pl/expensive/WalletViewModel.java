package pl.expensive;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;
import java.util.List;

import pl.expensive.storage.Currency;
import pl.expensive.storage.Transaction;

@AutoValue
abstract class WalletViewModel {
    abstract String name();
    abstract List<Transaction> transactions();
    abstract Currency currency();

    public static WalletViewModel create(String name, List<Transaction> transactions, Currency currency) {
        return new AutoValue_WalletViewModel(name, transactions, currency);
    }

    BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (!transactions().isEmpty()) {
            for (Transaction transaction : transactions()) {
                total = total.add(transaction.amount());
            }
        }
        return total;
    }
}
