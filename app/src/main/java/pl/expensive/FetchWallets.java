package pl.expensive;

import java.util.Collection;

import pl.expensive.storage.Wallet;
import pl.expensive.storage.WalletsStorage;
import rx.Single;

class FetchWallets {
    private final WalletsStorage storage;

    FetchWallets(WalletsStorage storage) {
        this.storage = storage;
    }

    Single<Collection<Wallet>> fetchWallets() {
        return Single.fromCallable(storage::list);
    }
}
