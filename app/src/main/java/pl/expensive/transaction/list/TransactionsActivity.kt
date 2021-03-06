package pl.expensive.transaction.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.activity_transactions.*
import org.threeten.bp.YearMonth
import pl.expensive.*
import pl.expensive.storage.TransactionDbo

sealed class ViewState {
    class Wallets(val adapterData: MutableList<Any>,
                  val title: CharSequence) : ViewState()

    class Empty(val title: CharSequence) : ViewState()
}

class TransactionsActivity : AppCompatActivity(), TransactionListFragment.TransactionListCallbacks {
    private val transactionsModel by lazy { Injector.app().transactionsModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        Injector.app().inject(this)

        setSupportActionBar(toolbar)

        // Animate FAB only once
        startContentAnimation(shouldAnimateFab = savedInstanceState == null)

        transactionsModel.showWallets(update)
    }

    override fun onResume() {
        super.onResume()
        transactionsFragment().attachCallbacks(this)
    }

    override fun onPause() {
        super.onPause()
        transactionsFragment().detachCallbacks()
    }

    private val update: (ViewState) -> Unit = {
        when (it) {
            is ViewState.Wallets -> {
                transactionsFragment().showTransactions(it.adapterData)
            }

            is ViewState.Empty -> {
                transactionsFragment().showEmpty()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transactions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 666 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("message")) {
                // Display new transactions including new/updated one
                transactionsModel.showWallets(update)
                toast(data.getStringExtra("message"))
            }
        }
    }

    override fun onTransactionSelected(transaction: TransactionDbo) {
        this@TransactionsActivity.startEditTransactionScreen(transaction)
    }

    override fun onMonthSelected(month: YearMonth) {
        startMonthOverviewScreen(month)
    }

    private fun transactionsFragment(): TransactionListFragment =
            supportFragmentManager.findFragmentById(R.id.vTransactionsListContainer) as TransactionListFragment
}
