package pl.expensive;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class WalletViewModel {
    abstract String name();

    static WalletViewModel create(String name) {
        return new AutoValue_WalletViewModel(name);
    }
}
