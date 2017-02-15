package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.view_new_transaction_placeholder_item.view.*
import pl.expensive.hideKeyboard
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import java.math.BigDecimal

class NewTransactionPlaceHolderViewHolder(itemView: View,
                                          val transactionStorage: TransactionStorage,
                                          val afterTransactionStored: (transition: Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private var isOpen = false

    fun update() {

        itemView.setOnClickListener { if (!isOpen) expand() else collapse() }

        itemView.vNewTransactionSubmit.setOnClickListener {
            if (validate()) {
                val amount = BigDecimal(itemView.vNewTransactionAmount.text.toString())
                val descText = itemView.vNewTransactionDescription.text.toString()

                val storedTransaction = Transaction.withdrawalWithAmount(amount = amount, desc = descText)
                transactionStorage.insert(storedTransaction)

                clearViews()
                collapse()

                afterTransactionStored(storedTransaction)
            }
        }
    }

    private fun expand() = itemView.vNewTransactionPlaceholderImg.animate()
            .rotationBy(45f)
            .withEndAction {
                isOpen = !isOpen
                itemView.vNewTransactionParent.visibility = VISIBLE
            }
            .start()

    private fun collapse() = itemView.vNewTransactionPlaceholderImg.animate()
            .rotationBy(-45f)
            .withEndAction {
                isOpen = !isOpen
                itemView.vNewTransactionParent.visibility = GONE
            }
            .start()

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

