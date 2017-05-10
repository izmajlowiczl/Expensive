package pl.expensive.wallet

// TODO: Include in file where it is used
sealed class ViewState {
    class Wallets(val adapterData: MutableList<Any>,
                  val title: CharSequence) : ViewState()

    class Empty(val title: CharSequence) : ViewState()
}
