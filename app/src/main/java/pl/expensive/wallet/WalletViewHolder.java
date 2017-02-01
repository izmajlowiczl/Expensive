package pl.expensive.wallet;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import pl.expensive.R;

public class WalletViewHolder extends RecyclerView.ViewHolder {
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
