package pl.expensive.storage;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AutoValue
public abstract class Transaction {
    public abstract UUID uuid();
    public abstract UUID wallet();
    public abstract BigDecimal amount();
    public abstract String currencyCode();
    public abstract long date();
    @Nullable
    public abstract String description();

    public static Transaction create(UUID uuid, UUID wallet, BigDecimal amount, String currencyCode, long time, String desc) {
        return new AutoValue_Transaction(uuid, wallet, amount, currencyCode, time, desc);
    }

    public static Transaction withdrawal(UUID wallet, BigDecimal amount, String currencyCode, String desc) {
        return create(UUID.randomUUID(), wallet, amount.negate(), currencyCode, new Date().getTime(), desc);
    }

    public static Transaction deposit(UUID wallet, BigDecimal amount, String currencyCode, String desc) {
        return create(UUID.randomUUID(), wallet, amount, currencyCode, new Date().getTime(), desc);
    }
}
