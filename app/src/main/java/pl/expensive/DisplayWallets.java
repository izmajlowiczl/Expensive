package pl.expensive;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import pl.expensive.storage.Wallet;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.functions.Func1;

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
                .map(new WalletsToViewModelsTransformer())
                .subscribe(new SingleSubscriber<Collection<WalletViewModel>>() {
                    @Override
                    public void onSuccess(Collection<WalletViewModel> value) {
                        if (value.isEmpty()) {
                            view.showEmpty();
                        } else {
                            view.showWallets(value);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        view.showFetchError();
                    }
                });
    }

    private static class WalletsToViewModelsTransformer implements Func1<Collection<Wallet>, Collection<WalletViewModel>> {
        @Override
        public Collection<WalletViewModel> call(Collection<Wallet> wallets) {
            Collection<WalletViewModel> result = new ArrayList<>(wallets.size());
            for (Wallet wallet : wallets) {
                result.add(WalletViewModel.create(wallet.name()));
            }
            return result;
        }
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