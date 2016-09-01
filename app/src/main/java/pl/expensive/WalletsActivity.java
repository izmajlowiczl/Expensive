package pl.expensive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

public class WalletsActivity extends AppCompatActivity {
    @Inject
    DisplayWallets displayWallets;

    private WalletsView vWallets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);

        Injector.app().inject(this);
        vWallets = (WalletsView) findViewById(R.id.wallets);

        showWallets();
    }

    private void showWallets() {
        displayWallets
                .setCallback(vWallets)
                .execute();
    }
}
