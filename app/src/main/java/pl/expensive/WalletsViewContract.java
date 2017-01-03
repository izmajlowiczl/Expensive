package pl.expensive;

import android.support.annotation.MainThread;

import java.util.Collection;

interface WalletsViewContract {
    @MainThread
    void showWallets(Collection<WalletViewModel> storedWallets);

    @MainThread
    void showEmpty();

    @MainThread
    void showFetchError();
}
