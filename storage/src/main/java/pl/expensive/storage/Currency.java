package pl.expensive.storage;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Currency {
    public static Currency create(String code, String format) {
        return new AutoValue_Currency(code, format);
    }

    public abstract String code();
    public abstract String format();
}
