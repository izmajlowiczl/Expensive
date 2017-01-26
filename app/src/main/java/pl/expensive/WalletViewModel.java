package pl.expensive;

import android.support.annotation.VisibleForTesting;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

import pl.expensive.storage.Currency;

@AutoValue
abstract class WalletViewModel {
    abstract String name();
    abstract BigDecimal total();
    abstract Currency currency();

    @VisibleForTesting
    static WalletViewModel create(String name) {
        return new AutoValue_WalletViewModel(name, BigDecimal.ZERO, null);
    }
    static WalletViewModel create(String name, BigDecimal total, Currency currency) {
        return new AutoValue_WalletViewModel(name, total, currency);
    }
}
