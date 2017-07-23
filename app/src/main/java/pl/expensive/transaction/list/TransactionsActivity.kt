package pl.expensive.transaction.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.view_quick_add_item.*
import pl.expensive.*
import pl.expensive.storage.asBigDecimal

sealed class ViewState {
    class Wallets(val adapterData: MutableList<Any>,
                  val title: CharSequence) : ViewState()

    class Empty(val title: CharSequence) : ViewState()
}

class TransactionsActivity : AppCompatActivity() {
    private val adapter by lazy {
        TransactionsAdapter(
                transactionClickFun = { transition ->
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
        configureQuickAddView()
        transactionsModel.showWallets(update)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transactions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_transaction -> {
                startNewTransactionCreatorScreen()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private val update: (ViewState) -> Unit = {
        when (it) {
            is ViewState.Wallets -> {
                vTransactions.show(true)

                adapter.data = it.adapterData

                supportActionBar!!.show()
//                supportActionBar!!.title = it.title
                toolbar_shadow.visibility = VISIBLE

                vTransactionsEmptyMsg.visibility = GONE

                vQuickAddInput.text.clear()
                vQuickAddInput.hideKeyboard()
            }

            is ViewState.Empty -> {
                vTransactions.show(false)

                toolbar_shadow.visibility = GONE
                supportActionBar!!.hide()

                vTransactionsEmptyMsg.visibility = VISIBLE

                vQuickAddInput.text.clear()
                vQuickAddInput.hideKeyboard()
            }
        }
    }

    private fun configureQuickAddView() {
        vQuickAddInput.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vQuickAddInput.hideKeyboard()

                val maybeAmountText = vQuickAddInput.text.toString()
                if (maybeAmountText.isNotBlank()) {
                    transactionsModel.quickAdd(maybeAmountText.asBigDecimal(), update)
                    vQuickAddInput.setText("")
                    return@OnEditorActionListener true
                }
            }
            return@OnEditorActionListener false
        })
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
