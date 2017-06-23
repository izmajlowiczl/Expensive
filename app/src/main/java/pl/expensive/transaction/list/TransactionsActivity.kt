package pl.expensive.transaction.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.activity_transactions.*
import pl.expensive.*

sealed class ViewState {
    class Wallets(val adapterData: MutableList<Any>,
                  val title: CharSequence) : ViewState()

    class Empty(val title: CharSequence) : ViewState()
}

class TransactionsActivity : AppCompatActivity() {
    private val adapter by lazy {
        TransactionsAdapter({ transition ->
            startEditTransactionScreen(transition)
        })
    }

    private val transactionsModel by lazy { Injector.app().transactionsModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        Injector.app().inject(this)

        setSupportActionBar(toolbar)

        vTransactions.layoutManager = LinearLayoutManager(this)
        vTransactions.adapter = adapter
        vTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        // Animate FAB only once
        startContentAnimation(shouldAnimateFab = savedInstanceState == null)
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

                supportActionBar!!.show()
                supportActionBar!!.title = it.title
                toolbar_shadow.visibility = VISIBLE

                vTransactionsEmptyMsg.visibility = GONE
            }

            is ViewState.Empty -> {
                vTransactions.show(false)

                toolbar_shadow.visibility = GONE
                supportActionBar!!.hide()

                vTransactionsEmptyMsg.visibility = VISIBLE
            }
        }
    }

    private fun startContentAnimation(shouldAnimateFab: Boolean) {
        val createWithdrawalAction: (View) -> Unit = {
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
                        .withEndAction {
                            setOnClickListener(createWithdrawalAction)
                        }
                        .start()
            } else {
                show(true)
                setOnClickListener(createWithdrawalAction)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 666 && resultCode == Activity.RESULT_OK) {
            // No need to refresh adapter. onResume was called and did it

            if (data != null && data.hasExtra("message")) {
                toast(data.getStringExtra("message"))
            }
        }
    }
}
