package pl.expensive.wallet

sealed class ViewState {
    class Wallets(val viewModels: WalletViewModel) : ViewState()
    class Loading : ViewState()
    class Error(val err: String) : ViewState()
    class Empty : ViewState()
}
