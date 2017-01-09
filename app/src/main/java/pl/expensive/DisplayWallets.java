package pl.expensive;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

class DisplayWallets {
    private final FetchWallets fetchWallets;
    @Nullable
    private Subscription fetchWalletsSubscription;

    @Inject
    DisplayWallets(FetchWallets fetchWallets) {
        this.fetchWallets = fetchWallets;
    }

    void runFor(final WalletsViewContract view) {
        fetchWalletsSubscription = fetchWallets.fetchWallets()
                .flatMapObservable(Observable::from)
                .filter(wallet -> !TextUtils.isEmpty(wallet.name())) // TODO: 09.01.2017 Replace with non-android version
                .map(wallet -> WalletViewModel.create(wallet.name()))
                .toList()
                .subscribe(walletViewModels -> {
                    if (walletViewModels.isEmpty()) {
                        view.showEmpty();
                    } else {
                        view.showWallets(walletViewModels);
                    }
                }, err -> view.showFetchError());
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