package pl.expensive;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.expensive.db.Wallet;

public class WalletsView extends RecyclerView {
    @Nullable // Lazy
    private WalletsAdapter walletsAdapter;

    public WalletsView(Context context) {
        super(context);
    }

    public WalletsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WalletsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @MainThread
    void showWallets(Collection<Wallet> storedWallets) {
        // Lazy load adapter on the first usage
        if (walletsAdapter == null) {
            walletsAdapter = new WalletsAdapter();
            setAdapter(walletsAdapter);
        }

        walletsAdapter.bind(storedWallets);

        setLayoutManager(new LinearLayoutManager(getContext()));
        setVisibility(VISIBLE);
    }

    @MainThread
    void showEmpty() {
        setVisibility(GONE);
    }

    private static class WalletsAdapter extends RecyclerView.Adapter<WalletViewHolder> {
        private final List<Wallet> wallets = new ArrayList<>();

        @Override
        public WalletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_wallet_item, parent, false);
            return new WalletViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WalletViewHolder holder, int position) {
            holder.bind(wallets.get(position));
        }

        @Override
        public int getItemCount() {
            return wallets.size();
        }

        void bind(Collection<Wallet> newWallets) {
            // TODO: 10.09.2016 Measure if making diff makes sense

            wallets.clear();
            wallets.addAll(newWallets);
            notifyDataSetChanged();
        }
    }

    private static class WalletViewHolder extends RecyclerView.ViewHolder {
        private final TextView vName;

        WalletViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.wallet_name);
        }

        void bind(@NonNull Wallet wallet) {
            vName.setText(wallet.name);
        }
    }
}