package pl.expensive.storage;

import java.util.Collection;
import java.util.UUID;

public interface TransactionStorage extends Storage<Transaction> {
    Collection<Transaction> select(UUID wallet);
}
