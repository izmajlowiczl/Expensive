package pl.expensive.wallet

sealed class ViewState {
    class Wallets(val viewModels: List<WalletViewModel>) : ViewState()
    class Empty : ViewState()
    class Loading : ViewState()
    class Error(val err: String) : ViewState()
}
