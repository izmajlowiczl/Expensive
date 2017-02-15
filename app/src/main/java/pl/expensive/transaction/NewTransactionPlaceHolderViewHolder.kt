package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.view_new_transaction_placeholder_item.view.*
import pl.expensive.collapseUp
import pl.expensive.expandDown
import pl.expensive.hideKeyboard
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import java.math.BigDecimal

class NewTransactionPlaceHolderViewHolder(itemView: View,
                                          val transactionStorage: TransactionStorage,
                                          val afterTransactionStored: (transition: Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update() {
        itemView.vNewTransactionTitleHeader.setOnClickListener { toggleView() }
        itemView.vNewTransactionSubmit.setOnClickListener {
            if (validate()) {
                val amount = BigDecimal(itemView.vNewTransactionAmount.text.toString())
                val descText = itemView.vNewTransactionDescription.text.toString()

                val storedTransaction = Transaction.withdrawalWithAmount(amount = amount, desc = descText)
                transactionStorage.insert(storedTransaction)

                clearViews()
                toggleView()

                afterTransactionStored(storedTransaction)
            }
        }
    }

    private var isOpen = false
    private fun toggleView() {
        itemView.vNewTransactionTitleHeader.isClickable = false
        itemView.vNewTransactionPlaceholderImg.rotateBy(if (!isOpen) 45f else -45f) {
            if (!isOpen) {
                itemView.vNewTransactionParent.expandDown()
            } else {
                itemView.vNewTransactionParent.collapseUp()
            }
            isOpen = !isOpen
            itemView.vNewTransactionTitleHeader.isClickable = true
        }
    }

    private fun View.rotateBy(value: Float, endAction: () -> Unit) {
        animate()
                .rotationBy(value)
                .withEndAction {
                    endAction()
                }.start()
    }

    private fun validate(): Boolean {
        var isValid = true
        with(itemView) {
            val amountText = vNewTransactionAmount.text.toString()
            if (amountText.isNullOrEmpty()) {
                vNewTransactionAmount.error = "Mandatory"
                isValid = false
            } else {
                vNewTransactionAmount.error = null
            }
        }
        return isValid
    }

    private fun clearViews() = with(itemView) {
        vNewTransactionAmount.text.clear()
        vNewTransactionDescription.text.clear()
        hideKeyboard()
    }
}

