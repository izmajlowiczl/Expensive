package pl.expensive.transaction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.a_new_transaction.*
import pl.expensive.*
import pl.expensive.R
import pl.expensive.storage.*
import pl.expensive.storage.Currency
import pl.expensive.wallet.WalletsService
import java.math.BigDecimal
import java.util.*

sealed class ViewState {
    class Create(val currency: Currency) : ViewState()

    class Edit(val transaction: UUID,
               val amount: BigDecimal,
               val currency: Currency,
               val description: CharSequence?) : ViewState()
}

class NewTransactionActivity : AppCompatActivity() {
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }
    private val walletsService: WalletsService by lazy { Injector.app().walletsService() }

    private lateinit var currentState: ViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_new_transaction)
        Injector.app().inject(this)

        val extras = intent.extras
        currentState =
                if (extras != null) {
                    ViewState.Edit(
                            transaction = extras.getString("transaction_uuid").toUUID(),
                            amount = extras.getString("transaction_amount").asBigDecimal(),
                            currency = walletsService.primaryWallet().currency,
                            description = extras.getString("transaction_desc"))
                } else {
                    ViewState.Create(currency = walletsService.primaryWallet().currency)
                }
        updateViewState(currentState)

        playEnterAnimation()
    }

    private val updateViewState: (ViewState) -> Unit = { state ->
        currentState = state

        when (state) {
            is ViewState.Create -> {
                vNewTransactionTitle.text = getString(R.string.add_new_spending)

                vNewTransactionAmount.afterTextChanged({
                    if (vNewTransactionAmount.text.isNotBlank()) {
                        vNewTransactionSave.tint(R.color.ready)
                    } else {
                        vNewTransactionSave.tint(R.color.colorTextLight)
                    }
                })

            }
            is ViewState.Edit -> {
                vNewTransactionTitle.text = getString(R.string.edit_spending)

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

        when (currentState) {
            is ViewState.Create -> {
                val state = currentState as ViewState.Create
                val storedTransaction = Transaction.withdrawalWithAmount(
                        amount = amountText.asBigDecimal(),
                        desc = descText,
                        currency = state.currency)


                transactionStorage.insert(storedTransaction)

                clearViews()
                finishWithResult(msg = getString(R.string.new_withdrawal_success_message, storedTransaction.amount.abs()))
            }

            is ViewState.Edit -> {
                val state = currentState as ViewState.Edit
                val storedTransaction = Transaction.withdrawalWithAmount(
                        uuid = state.transaction,
                        amount = amountText.asBigDecimal(),
                        desc = descText,
                        currency = state.currency)


                transactionStorage.update(storedTransaction)

                clearViews()
                finishWithResult(msg = getString(R.string.withdrawal_edited_success_message, storedTransaction.amount.abs()))
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
