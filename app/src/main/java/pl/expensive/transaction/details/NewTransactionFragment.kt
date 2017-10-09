package pl.expensive.transaction.details

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_new_transaction.*
import pl.expensive.*
import pl.expensive.R
import pl.expensive.storage.*
import pl.expensive.storage.Currency
import java.math.BigDecimal
import java.util.*

fun createNewTransactionFragment(extras: Bundle?): NewTransactionFragment {
    val fragment = NewTransactionFragment()
    if (extras != null) {
        fragment.arguments = extras
    }
    return fragment
}

class NewTransactionFragment : Fragment() {
    sealed class ViewState {
        class Create(val currency: Currency) : ViewState()

        class Edit(val transaction: UUID,
                   val amount: BigDecimal,
                   val currency: Currency,
                   val description: CharSequence?) : ViewState()
    }

    interface NewTransactionCallbacks {
        fun onNewTransaction(transaction: Transaction)
        fun onTransactionEdited(transaction: Transaction)
        fun onCanceled()
    }

    private val db: Database by lazy { Injector.app().db() }
    private val currencyRepository by lazy { Injector.app().currenciesRepository() }

    private lateinit var currentState: ViewState
    private var callback: NewTransactionCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentState = arguments?.toEditViewState()
                ?: toCreateState()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateViewState(currentState)
        playEnterAnimation()
    }


    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        if (activity is NewTransactionCallbacks) {
            callback = activity
        } else {
            throw RuntimeException(activity!!.toString() + " must implement NewTransactionCallbacks")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
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
            endAction { vNewTransactionClose.setOnClickListener { callback?.onCanceled() } }
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
                val storedTransaction = withdrawal(
                        amount = amountText.asBigDecimal(),
                        desc = descText,
                        currency = state.currency)

                insertTransaction(storedTransaction, db)

                clearViews()
                callback?.onNewTransaction(storedTransaction)
            }

            is ViewState.Edit -> {
                val state = currentState as ViewState.Edit

                val transactionToUpdateId = state.transaction
                val persisted = findTransaction(transactionToUpdateId, db)
                if (persisted != null) {
                    val storedTransaction = persisted.copy(
                            amount = amountText.asBigDecimalWithdrawal(),
                            description = descText)
                    updateTransaction(storedTransaction, db)

                    clearViews()
                    callback?.onTransactionEdited(storedTransaction)
                }
            }
        }
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
            description = getString("transaction_desc"),
            currency = currencyRepository.getDefaultCurrency())

    private fun toCreateState() = ViewState.Create(currencyRepository.getDefaultCurrency())
}

