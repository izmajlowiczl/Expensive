package pl.expensive.transaction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.a_new_transaction.*
import pl.expensive.*
import pl.expensive.R
import pl.expensive.storage.*
import pl.expensive.storage.Currency
import pl.expensive.wallet.WalletsService
import java.math.BigDecimal
import java.util.*

sealed class TransactionCreateMode {
    class Deposit : TransactionCreateMode()
    class Withdrawal : TransactionCreateMode()
}

sealed class ViewState {
    class Create(val mode: TransactionCreateMode,
                 val currency: Currency) : ViewState()

    class Edit(val mode: TransactionCreateMode,
               val transaction: UUID,
               val amount: BigDecimal,
               val currency: Currency,
               val description: CharSequence?) : ViewState()
}

class NewTransactionActivity : AppCompatActivity(), RevealBackgroundView.OnStateChangeListener {
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }
    private val walletsService: WalletsService by lazy { Injector.app().walletsService() }

    private lateinit var currentState: ViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_new_transaction)
        Injector.app().inject(this)

        val extras = intent.extras
        val mode =
                if (intent.getBooleanExtra("is_deposit", false))
                    TransactionCreateMode.Deposit()
                else
                    TransactionCreateMode.Withdrawal()
        currentState =
                if (extras != null && extras.containsKey("transaction_uuid")) {
                    ViewState.Edit(mode,
                            transaction = extras.getString("transaction_uuid").toUUID(),
                            amount = extras.getString("transaction_amount").asBigDecimal(),
                            currency = walletsService.primaryWallet().currency,
                            description = extras.getString("transaction_desc"))
                } else {
                    ViewState.Create(mode,
                            currency = walletsService.primaryWallet().currency)
                }
        updateViewState(currentState)

        setupRevealBackground(savedInstanceState)
    }

    private val updateViewState: (ViewState) -> Unit = { state ->
        currentState = state

        when (state) {
            is ViewState.Create -> {
                vNewTransactionTitle.text =
                        when (state.mode) {
                            is TransactionCreateMode.Deposit -> getString(R.string.add_new_deposit)
                            is TransactionCreateMode.Withdrawal -> getString(R.string.add_new_spending)
                        }

                vNewTransactionAmount.afterTextChanged({
                    if (vNewTransactionAmount.text.isNotBlank()) {
                        vNewTransactionSave.tint(R.color.ready)
                    } else {
                        vNewTransactionSave.tint(R.color.colorTextLight)
                    }
                })

            }
            is ViewState.Edit -> {
                vNewTransactionTitle.text =
                        when (state.mode) {
                            is TransactionCreateMode.Deposit -> getString(R.string.edit_deposit)
                            is TransactionCreateMode.Withdrawal -> getString(R.string.edit_spending)
                        }

                with(vNewTransactionAmount) {
                    setText(state.amount.toString())
                    afterTextChanged({
                        if (text.toString() != state.amount.toString()) {
                            vNewTransactionSave.tint(R.color.ready)
                        } else {
                            vNewTransactionSave.tint(R.color.colorTextLight)
                        }
                    })
                }
                with(vNewTransactionDescription) {
                    setText(state.description)
                    afterTextChanged {
                        if (text.toString() != state.description ?: "") {
                            vNewTransactionSave.tint(R.color.ready)
                        } else {
                            vNewTransactionSave.tint(R.color.colorTextLight)
                        }
                    }
                }
            }
        }
    }

    private fun setupRevealBackground(savedInstanceState: Bundle?) = with(revealLayer) {
        setFillPaintColor(ResourcesCompat.getColor(resources, R.color.window_bg, theme))
        setOnStateChangeListener(this@NewTransactionActivity)
        if (savedInstanceState == null) {
            val startingLocation = intent.getIntArrayExtra("loc")
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    startFromLocation(startingLocation)
                    return true
                }
            })
        } else {
            setToFinishedFrame()
        }
    }

    override fun onStateChange(state: Int) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            vNewTransactionScrollParent.show(true)
            playEnterAnimation()
        }
    }

    private fun playEnterAnimation() {
        vNewTransactionClose.startAnimation(vNewTransactionClose.rotate(0f, 45f).apply {
            endAction { vNewTransactionClose.setOnClickListener { finishCanceled() } }
        })
        vNewTransactionSave.startAnimation(vNewTransactionSave.scaleFromMiddle(0f, 1f).apply {
            endAction {
                vNewTransactionSave.show(true)
                vNewTransactionSave.setOnClickListener {
                    if (validate()) {
                        handleSaveButtonClick()
                    }
                }
            }
        })
        vNewTransactionParent.startAnimation(vNewTransactionParent.expandDown())
    }

    private fun handleSaveButtonClick() {
        val amountText = vNewTransactionAmount.text.toString()
        val descText = vNewTransactionDescription.text.toString()

        val primaryWallet = walletsService.primaryWallet()

        when (currentState) {
            is ViewState.Create -> {
                val state = currentState as ViewState.Create
                val storedTransaction =
                        when (state.mode) {
                            is TransactionCreateMode.Deposit -> Transaction.depositWithAmount(
                                    amount = amountText.asBigDecimal(),
                                    desc = descText,
                                    currency = state.currency)

                            is TransactionCreateMode.Withdrawal -> Transaction.withdrawalWithAmount(
                                    amount = amountText.asBigDecimal(),
                                    desc = descText,
                                    currency = state.currency)

                        }
                transactionStorage.insert(storedTransaction)

                clearViews()
                val message = when (state.mode) {
                    is TransactionCreateMode.Deposit -> getString(R.string.new_deposit_success_message, storedTransaction.amount.abs())
                    is TransactionCreateMode.Withdrawal -> getString(R.string.new_withdrawal_success_message, storedTransaction.amount.abs())
                }
                finishWithResult(msg = message)
            }

            is ViewState.Edit -> {
                val state = currentState as ViewState.Edit
                val storedTransaction =
                        when (state.mode) {
                            is TransactionCreateMode.Deposit -> Transaction.depositWithAmount(
                                    uuid = state.transaction,
                                    amount = amountText.asBigDecimal(),
                                    desc = descText,
                                    currency = state.currency)
                            is TransactionCreateMode.Withdrawal -> Transaction.withdrawalWithAmount(
                                    uuid = state.transaction,
                                    amount = amountText.asBigDecimal(),
                                    desc = descText,
                                    currency = primaryWallet.currency)
                        }

                transactionStorage.update(storedTransaction)

                clearViews()

                val message = when (state.mode) {
                    is TransactionCreateMode.Deposit -> getString(R.string.deposit_edited_success_message, storedTransaction.amount.abs())
                    is TransactionCreateMode.Withdrawal -> getString(R.string.withdrawal_edited_success_message, storedTransaction.amount.abs())
                }
                finishWithResult(msg = message)
            }

        }
    }

    private fun finishCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun finishWithResult(msg: String) {
        val extras = Bundle()
        extras.putString("message", msg)
        setResult(Activity.RESULT_OK, Intent().putExtras(extras))
        finish()
    }

    private fun validate(): Boolean {
        var isValid = true
        val amountText = vNewTransactionAmount.text.toString()
        if (amountText.isNullOrEmpty()) {
            vNewTransactionAmount.error = getString(R.string.err_mandatory)
            isValid = false
        } else {
            vNewTransactionAmount.error = null
        }
        return isValid
    }

    private fun clearViews() {
        vNewTransactionAmount.text.clear()
        vNewTransactionDescription.text.clear()
        vNewTransactionAmount.hideKeyboard()
    }
}
