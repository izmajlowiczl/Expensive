package pl.expensive;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import pl.expensive.storage.Transaction;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;
import rx.Observable;
import rx.Single;
import rx.Subscription;

class DisplayWallets {
    @Nullable
    private Subscription fetchWalletsSubscription;

    private final WalletsStorage walletsStorage;
    private final TransactionStorage transactionStorage;

    @Inject
    DisplayWallets(WalletsStorage walletsStorage, TransactionStorage transactionStorage) {
        this.walletsStorage = walletsStorage;
        this.transactionStorage = transactionStorage;
    }

    void runFor(final WalletsViewContract view) {
        fetchWalletsSubscription = Single.fromCallable(walletsStorage::list)
                .flatMapObservable(Observable::from)
                .filter(wallet -> !TextUtils.isEmpty(wallet.name())) // TODO: 09.01.2017 Replace with non-android version
                .map(wallet -> WalletViewModel.create(
                        wallet.name(),
                        (List<Transaction>) transactionStorage.select(wallet.uuid()),
                        wallet.currency()))
                .toList()
                .subscribe(walletViewModels -> {
                    if (walletViewModels.isEmpty()) {
                        view.showEmpty();
                    } else {
                        view.showWallets(walletViewModels);
                    }
                }, err -> {
                    Log.e("XXX", err.toString());
                    view.showFetchError();
                });
    }

    void dispose() {
        if (!isDisposed()) {
            fetchWalletsSubscription.unsubscribe();
        }
    }

    private boolean isDisposed() {
        return fetchWalletsSubscription == null || fetchWalletsSubscription.isUnsubscribed();
    }
}
