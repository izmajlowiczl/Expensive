package pl.expensive.transaction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.a_new_transaction.*
import pl.expensive.*
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage.asBigDecimal
import pl.expensive.wallet.WalletsService
import java.util.*

class NewTransactionActivity : AppCompatActivity(), RevealBackgroundView.OnStateChangeListener {
    private val transactionStorage: TransactionStorage by lazy { Injector.app().transactions() }
    private val walletsService: WalletsService by lazy { Injector.app().walletsService() }


    private var isDepositTransaction: Boolean = false
    private var isInEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_new_transaction)
        Injector.app().inject(this)

        val extras = intent.extras
        isDepositTransaction = intent.getBooleanExtra("is_deposit", false)
        isInEditMode = isEditMode(extras)

        updateTitleView()
        updateAmountView(extras)
        updateDescriptionView(extras)

        setupRevealBackground(savedInstanceState)
    }

    private fun updateTitleView() {
        vNewTransactionTitle.text =
                if (isDepositTransaction) {
                    getString(if (isInEditMode) R.string.edit_deposit else R.string.add_new_deposit)
                } else {
                    getString(if (isInEditMode) R.string.edit_spending else R.string.add_new_spending)
                }
    }

    private fun updateAmountView(extras: Bundle) {
        if (isInEditMode) {
            vNewTransactionAmount.setText(extras.getString("transaction_amount"))
        }
        vNewTransactionAmount.afterTextChanged({
            // When is expanded and has all mandatory fields filled. Change color of save button
            if (vNewTransactionAmount.text.isNotBlank()) {
                vNewTransactionSave.tint(R.color.ready)
            } else {
                vNewTransactionSave.tint(R.color.colorTextLight)
            }
        })
    }

    private fun updateDescriptionView(extras: Bundle) {
        // In edit mode, mandatory fields has to be filled and some data changed to change color of save button
        if (isInEditMode) {
            vNewTransactionDescription.setText(extras.getString("transaction_desc"))
        }
        if (isInEditMode) {
            vNewTransactionDescription.afterTextChanged {
                if (dataChangedInEditMode(extras)) {
                    vNewTransactionSave.tint(R.color.ready)
                } else {
                    vNewTransactionSave.tint(R.color.colorTextLight)
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
                    handleSaveButtonClick()
                }
            }
        })
        vNewTransactionParent.startAnimation(vNewTransactionParent.expandDown())
    }

    private fun handleSaveButtonClick() {
        if (validate()) {
            val amountText = vNewTransactionAmount.text.toString()
            val descText = vNewTransactionDescription.text.toString()

            val primaryWallet = walletsService.primaryWallet()

            // In Edit Mode check if anything changed
            val extras = intent.extras
            if (isEditMode(extras)) {
                val oldAmount = extras.getString("transaction_amount")
                val oldDesc = extras.getString("transaction_desc")

                if (amountText != oldAmount || descText != oldDesc) {
                    val storedTransaction =
                            if (isDepositTransaction) {
                                Transaction.depositWithAmount(
                                        uuid = UUID.fromString(extras.getString("transaction_uuid")),
                                        amount = amountText.asBigDecimal(),
                                        desc = descText,
                                        currency = primaryWallet.currency)
                            } else {
                                Transaction.withdrawalWithAmount(
                                        uuid = UUID.fromString(extras.getString("transaction_uuid")),
                                        amount = amountText.asBigDecimal(),
                                        desc = descText,
                                        currency = primaryWallet.currency)
                            }
                    transactionStorage.update(storedTransaction)

                    clearViews()
                    // TODO: Change message for edit confirmation
                    finishWithResult(storedTransaction)
                }
            } else {
                val storedTransaction = if (isDepositTransaction) {
                    Transaction.depositWithAmount(
                            amount = amountText.asBigDecimal(),
                            desc = descText,
                            currency = primaryWallet.currency)
                } else {
                    Transaction.withdrawalWithAmount(
                            amount = amountText.asBigDecimal(),
                            desc = descText,
                            currency = primaryWallet.currency)
                }
                transactionStorage.insert(storedTransaction)

                clearViews()
                finishWithResult(storedTransaction)
            }
        }
    }

    private fun isEditMode(extras: Bundle?) = extras != null && extras.containsKey("transaction_uuid")

    private fun finishCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun finishWithResult(transaction: Transaction) {
        val extras = Bundle()
        extras.putParcelable("storedTransaction", transaction)
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
