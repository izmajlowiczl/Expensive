package pl.expensive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class WalletsActivity extends AppCompatActivity {
    private DisplayWallets fetchWallets;
    private WalletsViewContract vWallets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallets);

        Injector.app().inject(this);
        vWallets = (WalletsView) findViewById(R.id.wallets);

        fetchWallets = Injector.app().displayWallets();
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
