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

class NewTransactionActivity : AppCompatActivity() {
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_new_transaction)
        Injector.app().inject(this)

        playEnterAnimation()

        vNewTransactionAmount.afterTextChanged {
            // When is expanded and has all mandatory fields filled. Change color of save button
            if (vNewTransactionAmount.text.isNotBlank()) {
                vNewTransactionSave.tint(pl.expensive.R.color.ready)
            } else {
                vNewTransactionSave.tint(pl.expensive.R.color.colorTextLight)
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
                        val amountText = vNewTransactionAmount.text.toString()
                        val descText = vNewTransactionDescription.text.toString()
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
        })
        vNewTransactionParent.startAnimation(vNewTransactionParent.expandDown())
    }

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
