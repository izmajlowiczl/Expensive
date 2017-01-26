package pl.expensive;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

@AutoValue
abstract class WalletViewModel {
    abstract String name();
    abstract BigDecimal total();

    static WalletViewModel create(String name) {
        return new AutoValue_WalletViewModel(name, BigDecimal.ZERO);
    }
    static WalletViewModel create(String name, BigDecimal total) {
        return new AutoValue_WalletViewModel(name, total);
    }
}
