package pl.expensive.wallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wallets.*
import pl.expensive.Injector
import pl.expensive.R

class WalletsActivity : AppCompatActivity() {
    private lateinit var fetchWallets: DisplayWallets

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)

        Injector.app().inject(this)

        fetchWallets = Injector.app().displayWallets()
    }

    override fun onStart() {
        super.onStart()
        showWallets()
    }

    private fun showWallets() {
        fetchWallets.runFor(wallets)
    }
}
