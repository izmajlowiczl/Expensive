package pl.expensive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.WalletsStorage;

public class WalletsActivity extends AppCompatActivity {
    @Inject
    WalletsStorage walletsStorage;
    @Inject
    TransactionStorage transactionStorage;

    DisplayWallets fetchWallets;
    private WalletsViewContract vWallets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallets);

        Injector.app().inject(this);
        vWallets = (WalletsView) findViewById(R.id.wallets);

        fetchWallets = new DisplayWallets(walletsStorage, transactionStorage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showWallets();
    }

    private void showWallets() {
        fetchWallets.runFor(vWallets);
    }

}
