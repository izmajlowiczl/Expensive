package pl.expensive.storage;

import com.google.auto.value.AutoValue;

import java.util.UUID;

@AutoValue
public abstract class Wallet {
    public static Wallet create(UUID uuid, String name, Currency currency) {
        return new AutoValue_Wallet(uuid, name, currency);
    }

    public abstract UUID uuid();
    public abstract String name();
    public abstract Currency currency();
}
