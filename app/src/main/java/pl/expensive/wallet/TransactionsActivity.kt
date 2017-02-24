package pl.expensive.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.activity_wallets.*
import pl.expensive.*
import pl.expensive.transaction.NewTransactionActivity
import pl.expensive.transaction.TransactionsAdapter

class TransactionsActivity : AppCompatActivity() {
    private val adapter by lazy {
        TransactionsAdapter({ touchPos, transition ->
            startEditTransactionScreen(touchPos, transition)
        })
    }

    private val transactionsModel by lazy { Injector.app().transactionsModel() }

    private var shouldAnimateFab = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)
        Injector.app().inject(this)

        // Animate FAB only once
        shouldAnimateFab = savedInstanceState == null

        setSupportActionBar(toolbar)

        vTransactions.layoutManager = LinearLayoutManager(this)
        vTransactions.adapter = adapter
        vTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        startContentAnimation()
    }

    override fun onResume() {
        super.onResume()
        transactionsModel.showWallets(update)
    }

    private val update: (ViewState) -> Unit = {
        when (it) {
            is ViewState.Wallets -> {
                vTransactions.show(true)

                adapter.data = it.adapterData
                supportActionBar!!.title = it.title
            }

            is ViewState.Empty -> {
                vTransactions.show(false)

                adapter.data = mutableListOf<Any>()
                supportActionBar!!.title = it.title
            }
        }
    }

    private fun startContentAnimation() {
        val fabClickListener: (View) -> Unit = {
            startNewTransactionCreatorScreen()
        }
        with(vCreateTransactionFab) {
            if (shouldAnimateFab) {
                translationY = 2 * resources.getDimension(R.dimen.fab_size) // Hide below screen
                animate() // Pop up from bottom
                        .translationY(0f)
                        .setInterpolator(OvershootInterpolator(1f))
                        .setStartDelay(300)
                        .setDuration(longAnim().toLong())
                        .withEndAction { setOnClickListener(fabClickListener) }
                        .start()
            } else {
                show(true)
                setOnClickListener(fabClickListener)
            }
        }
    }

    private fun startNewTransactionCreatorScreen() {
        val intent = Intent(this@TransactionsActivity, NewTransactionActivity::class.java)
                .putExtra("loc", vCreateTransactionFab.middleOnScreen())
        startActivityForResult(intent, 666)
        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 666 && resultCode == Activity.RESULT_OK) {

            // No need to refresh adapter. onResume was called and did it

            // TODO("Instead of passing string, pass value. Show deposit/withdrawal message")
            val storedTransactionAmount = data?.getStringExtra("storedTransaction") ?: ""
            if (storedTransactionAmount.isNotBlank()) {
                toast(getString(R.string.new_transaction_success_message, storedTransactionAmount))
            }
        }
    }
}

