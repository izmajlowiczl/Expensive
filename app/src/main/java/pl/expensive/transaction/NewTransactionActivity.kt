package pl.expensive.transaction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.a_new_transaction.*
import pl.expensive.*
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage._Seeds
import java.math.BigDecimal
import java.util.*

class NewTransactionActivity : AppCompatActivity() {
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_new_transaction)
        Injector.app().inject(this)

        val extras = intent.extras
        val isInEditMode = isEditMode(extras)
        if (isInEditMode) {
            vNewTransactionAmount.setText(extras.getString("transaction_amount"))
            vNewTransactionDescription.setText(extras.getString("transaction_desc"))
        }

        vNewTransactionTitle.text = getString(if (isInEditMode) R.string.edit_spending else R.string.add_new_spending)

        playEnterAnimation()

        vNewTransactionAmount.afterTextChanged({
            // When is expanded and has all mandatory fields filled. Change color of save button
            if (vNewTransactionAmount.text.isNotBlank()) {
                vNewTransactionSave.tint(pl.expensive.R.color.ready)
            } else {
                vNewTransactionSave.tint(pl.expensive.R.color.colorTextLight)
            }
        })

        // In edit mode, mandatory fields has to be filled and some data changed to change color of save button
        if (isInEditMode) {
            vNewTransactionDescription.afterTextChanged {
                if (dataChangedInEditMode(extras)) {
                    vNewTransactionSave.tint(pl.expensive.R.color.ready)
                } else {
                    vNewTransactionSave.tint(pl.expensive.R.color.colorTextLight)
                }
            }
        }
    }

    private fun dataChangedInEditMode(extras: Bundle): Boolean {
        val validInEditMode: Boolean = if (isEditMode(extras)) {
            val oldAmount = extras.getString("transaction_amount")
            val oldDesc = extras.getString("transaction_desc")
            val amountText = vNewTransactionAmount.text.toString()
            val descText = vNewTransactionDescription.text.toString()

            amountText != oldAmount || descText != oldDesc
        } else {
            // Create mode, continue...
            true
        }
        return validInEditMode
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
                        val amountText = vNewTransactionAmount.text.toString()
                        val descText = vNewTransactionDescription.text.toString()

                        // In Edit Mode check if anything changed
                        val extras = intent.extras
                        if (isEditMode(extras)) {
                            val oldAmount = extras.getString("transaction_amount")
                            val oldDesc = extras.getString("transaction_desc")

                            if (amountText != oldAmount || descText != oldDesc) {
                                val storedTransaction = Transaction.withdrawalWithAmount(
                                        uuid = UUID.fromString(extras.getString("transaction_uuid")),
                                        amount = BigDecimal(amountText),
                                        desc = descText,
                                        category = _Seeds.GROCERY)
                                transactionStorage.update(storedTransaction)

                                clearViews()
                                // TODO: Change message for edit confirmation
                                finishWithResult(storedTransaction)
                            }
                        } else {
                            val storedTransaction = Transaction.withdrawalWithAmount(
                                    amount = BigDecimal(amountText),
                                    desc = descText,
                                    category = _Seeds.GROCERY)
                            transactionStorage.insert(storedTransaction)
                            clearViews()
                            finishWithResult(storedTransaction)
                        }
                    }
                }
            }
        })
        vNewTransactionParent.startAnimation(vNewTransactionParent.expandDown())
    }

    private fun isEditMode(extras: Bundle?) = extras != null && extras.containsKey("transaction_uuid")

    private fun finishCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun finishWithResult(transaction: Transaction) {
        setResult(Activity.RESULT_OK, Intent()
                .putExtra("storedTransaction", transaction.currency.formatValue(money = transaction.amount)))
        finish()
    }

    private fun validate(): Boolean {
        var isValid = true
        val amountText = vNewTransactionAmount.text.toString()
        if (amountText.isNullOrEmpty()) {
            vNewTransactionAmount.error = getString(pl.expensive.R.string.err_mandatory)
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
