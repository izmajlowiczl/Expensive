package pl.expensive.wallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wallets.*
import pl.expensive.Injector
import pl.expensive.R

class WalletsActivity : AppCompatActivity() {
    private val fetchWallets: DisplayWallets
            by lazy { Injector.app().displayWallets() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)

        Injector.app().inject(this)
    }

    override fun onStart() {
        super.onStart()
        showWallets()
    }

    private fun showWallets() {
        fetchWallets.runFor(wallets)
    }
}
