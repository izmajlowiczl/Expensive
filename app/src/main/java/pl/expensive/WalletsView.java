package pl.expensive;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private static class WalletsAdapter extends RecyclerView.Adapter<WalletViewHolder> {
        private final List<WalletViewModel> wallets = new ArrayList<>();

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

        void bind(Collection<WalletViewModel> newWallets) {
            wallets.clear();
            wallets.addAll(newWallets);
            notifyDataSetChanged();
        }
    }

    private static class WalletViewHolder extends RecyclerView.ViewHolder {
        private final TextView vName;
        private final TextView vTotalAmount;

        WalletViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.wallet_name);
            vTotalAmount = (TextView) itemView.findViewById(R.id.wallet_total);
        }

        void bind(WalletViewModel viewModel) {
            vName.setText(viewModel.getName());
            vTotalAmount.setText(viewModel.formattedTotal());
        }
    }
}
