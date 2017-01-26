package pl.expensive.transaction;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.expensive.storage.Transaction;

public final class TransactionGrouper {
    public static Map<LocalDateTime, List<Transaction>> group(List<Transaction> sortedTransactions) {
        if (sortedTransactions.isEmpty()) {
            return Collections.emptyMap();
        }

        LocalDateTime prev = sortedTransactions.get(0).toLocalDate();

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(sortedTransactions.get(0));

        Map<LocalDateTime, List<Transaction>> result = new LinkedHashMap<>();
        result.put(prev, transactions);

        for (int i = 1; i < sortedTransactions.size(); i++) {
            Transaction transaction = sortedTransactions.get(i);
            LocalDateTime date = transaction.toLocalDate();

            if (date.toLocalDate().isEqual(prev.toLocalDate())) {
                result.get(date).add(transaction);
            } else {
                prev = date;
                List<Transaction> t = new ArrayList<>();
                t.add(transaction);
                result.put(date, t);
            }
        }

        return result;
    }
}
