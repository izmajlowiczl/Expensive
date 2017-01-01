package pl.expensive;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.Collection;

import javax.inject.Inject;

import pl.expensive.storage.Wallet;
import pl.expensive.storage.WalletsStorage;

class DisplayWallets extends AsyncTask<Void, Void, Collection<Wallet>> {
    private final WalletsStorage model;
    private WeakReference<WalletsView> view;

    @Inject
    DisplayWallets(WalletsStorage model) {
        this.model = model;
    }

    public DisplayWallets setCallback(WalletsView callback) {
        view = new WeakReference(callback);
        return this;
    }

    @Override
    protected Collection<Wallet> doInBackground(Void... voids) {
        Collection<Wallet> storedWallets = model.list();
        return storedWallets;
    }

    @Override
    protected void onPostExecute(Collection<Wallet> wallets) {
        super.onPostExecute(wallets);
        WalletsView target = view.get();
        if (target == null) {
            return;
        }

        if (wallets.isEmpty()) {
            target.showEmpty();
        } else {
            target.showWallets(wallets);
        }

        view.clear();
    }
}