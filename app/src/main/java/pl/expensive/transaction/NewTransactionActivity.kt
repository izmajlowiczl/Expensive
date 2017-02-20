package pl.expensive.transaction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.a_new_transaction.*
import org.jetbrains.anko.find
import pl.expensive.*
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
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

        vNewTransactionRepeat.setOnClickListener {
            toggleRepeatModes()
        }
    }

    sealed class RepeatMode(val id: Int) {
        class None : RepeatMode(R.id.vNewTransactionRepeatModesNoRepeat)
        class RepeatDaily : RepeatMode(R.id.vNewTransactionRepeatModesDaily)
        class RepeatMonthly : RepeatMode(R.id.vNewTransactionRepeatModesMonthly)

        companion object {
            fun find(id: Int): RepeatMode = when (id) {
                R.id.vNewTransactionRepeatModesDaily -> RepeatMode.RepeatDaily()
                R.id.vNewTransactionRepeatModesMonthly -> RepeatMode.RepeatMonthly()
                R.id.vNewTransactionRepeatModesNoRepeat -> RepeatMode.None()
                else -> RepeatMode.None()
            }
        }
    }

    // TODO: This state should be persisted
    private var currentRepeatMode: RepeatMode = RepeatMode.None()
    var isRepeatModesOpen = false
    private fun toggleRepeatModes() {
        with(vNewTransactionRepeatModes) {
            if (isRepeatModesOpen) {
                startAnimation(collapseUp().apply {
                    endAction {
                        vNewTransactionRepeatModes.setOnCheckedChangeListener(null)
                    }
                })
            } else {
                startAnimation(expandDown().apply {
                    endAction {
                        val changeListener: (RadioGroup, Int) -> Unit = { group, checkedId ->
                            group.find<RadioButton>(currentRepeatMode.id).apply {
                                show(true)
                            }
                            group.find<RadioButton>(checkedId).apply {
                                show(false)
                                vNewTransactionRepeatTitle.text = text
                            }

                            currentRepeatMode = RepeatMode.find(checkedId)
                            toggleRepeatModes() // close modes
                        }
                        vNewTransactionRepeatModes.setOnCheckedChangeListener(changeListener)
                    }
                })
            }
        }
        isRepeatModesOpen = !isRepeatModesOpen
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
                        val (amount, descText) = gatSaveParams()
                        val storedTransaction = Transaction.withdrawalWithAmount(amount = BigDecimal(amount), desc = descText)
                        transactionStorage.insert(storedTransaction)

                        // TODO Handle repeat mode


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

    private fun gatSaveParams(): SaveTransactionViewModel {
        val amount = vNewTransactionAmount.text.toString()
        val descText = vNewTransactionDescription.text.toString()
        return SaveTransactionViewModel(amount, descText)
    }

    data class SaveTransactionViewModel(val amount: String, val desc: String)
}
