package pl.expensive.wallet;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.Toast;

import java.util.Collection;

public class WalletsView extends RecyclerView implements WalletsViewContract {
    private WalletsAdapter walletsAdapter;

    public WalletsView(Context context) {
        super(context);
        init(context);
    }

    public WalletsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WalletsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context));
        setVisibility(VISIBLE);
        walletsAdapter = new WalletsAdapter();
        setAdapter(walletsAdapter);
    }

    @Override
    @MainThread
    public void showWallets(Collection<WalletViewModel> storedWallets) {
        walletsAdapter.bind(storedWallets);
    }

    @Override
    @MainThread
    public void showEmpty() {
        setVisibility(GONE);
    }

    @Override
    @MainThread
    public void showFetchError() {
        Toast.makeText(getContext(), "Cannot fetch wallets", Toast.LENGTH_SHORT).show();
    }
}
