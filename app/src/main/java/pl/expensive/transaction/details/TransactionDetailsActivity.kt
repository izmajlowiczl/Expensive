package pl.expensive.transaction.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_transaction_details.*
import pl.expensive.*
import pl.expensive.R
import pl.expensive.storage.*
import pl.expensive.storage.Currency
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.OTHER
import java.math.BigDecimal
import java.util.*

sealed class ViewState {
    class Create(val currency: Currency = EUR) : ViewState()

    class Edit(val transaction: UUID,
               val amount: BigDecimal,
               val currency: Currency = EUR,
               val description: CharSequence?) : ViewState()
}

class NewTransactionActivity : AppCompatActivity() {
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }

    private lateinit var currentState: ViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
        Injector.app().inject(this)

        currentState = intent.extras?.toEditViewState()
                ?: ViewState.Create()
        updateViewState(currentState)

        playEnterAnimation()
    }

    private val updateViewState: (ViewState) -> Unit = { state ->
        currentState = state

        when (state) {
            is ViewState.Create -> {
                vNewTransactionTitle.text = getString(R.string.add_new_spending)
                vNewTransactionAmount.afterTextChanged({
                    val color = if (vNewTransactionAmount.text.isNotBlank()) R.color.ready else R.color.colorTextLight
                    vNewTransactionSave.tint(color)
                })
            }

            is ViewState.Edit -> {
                vNewTransactionTitle.text = getString(R.string.edit_spending)

                with(vNewTransactionAmount) {
                    setText(state.amount.toString())
                    afterTextChanged({
                        val color = if (text.toString() != state.amount.toString()) R.color.ready else R.color.colorTextLight
                        vNewTransactionSave.tint(color)
                    })
                }
                with(vNewTransactionDescription) {
                    setText(state.description)
                    afterTextChanged {
                        val shouldEnableSaveButton = text.toString() != state.description ?: ""
                        val color = if (shouldEnableSaveButton) R.color.ready else R.color.colorTextLight
                        vNewTransactionSave.tint(color)
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
                        category = OTHER,
                        currency = state.currency)

                transactionStorage.insert(storedTransaction)

                clearViews()
                finishWithResult(getString(R.string.new_withdrawal_success_message, storedTransaction.amount.abs()))
            }

            is ViewState.Edit -> {
                val state = currentState as ViewState.Edit
                val storedTransaction = Transaction.withdrawalWithAmount(
                        uuid = state.transaction,
                        amount = amountText.asBigDecimal(),
                        desc = descText,
                        category = OTHER,
                        currency = state.currency)

                transactionStorage.update(storedTransaction)

                clearViews()
                finishWithResult(getString(R.string.withdrawal_edited_success_message, storedTransaction.amount.abs()))
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

    private fun Bundle.toEditViewState(): ViewState.Edit = ViewState.Edit(
            transaction = getString("transaction_uuid").toUUID(),
            amount = getString("transaction_amount").asBigDecimal(),
            description = getString("transaction_desc"))
}
