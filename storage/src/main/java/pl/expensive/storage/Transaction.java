package pl.expensive.storage;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java.math.BigDecimal;
import java.util.UUID;

@AutoValue
public abstract class Transaction {
    public abstract UUID uuid();
    public abstract UUID wallet();
    public abstract BigDecimal amount();
    public abstract String currencyCode();
    // time in millis
    public abstract long date();
    @Nullable
    public abstract String description();

    public static Transaction create(UUID uuid, UUID wallet, BigDecimal amount, String currencyCode, LocalDateTime date, String desc) {
        return new AutoValue_Transaction(uuid, wallet, amount, currencyCode, toMillisUTC(date), desc);
    }

    public static Transaction create(UUID uuid, UUID wallet, BigDecimal amount, String currencyCode, long time, String desc) {
        return new AutoValue_Transaction(uuid, wallet, amount, currencyCode, time, desc);
    }

    public static Transaction withdrawal(UUID wallet, BigDecimal amount, String currencyCode, String desc) {
        return create(UUID.randomUUID(), wallet, amount.negate(), currencyCode, toMillisUTC(LocalDateTime.now()), desc);
    }

    public static Transaction deposit(UUID wallet, BigDecimal amount, String currencyCode, String desc) {
        return create(UUID.randomUUID(), wallet, amount, currencyCode, toMillisUTC(LocalDateTime.now()), desc);
    }

    public LocalDateTime toLocalDate() {
        return Instant.ofEpochMilli(date())
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }

    private static long toMillisUTC(LocalDateTime date) {
        return date.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
