package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.*
import android.view.animation.Animation
import kotlinx.android.synthetic.main.view_new_transaction_placeholder_item.view.*
import pl.expensive.*
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import java.math.BigDecimal

class NewTransactionPlaceHolderViewHolder(itemView: View,
                                          val transactionStorage: TransactionStorage,
                                          val afterTransactionStored: (transition: Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update() = with(itemView) {
        vNewTransactionPlaceholderSave.visibility = if (isOpen) VISIBLE else INVISIBLE
        vNewTransactionParent.visibility = if (isOpen) VISIBLE else GONE
        vNewTransactionTitleHeader.setOnClickListener { toggleView() }
    }

    val saveClickListener: (View) -> Unit = {
        if (validate()) {
            val (amount, descText) = this.gatSaveParams()
            val storedTransaction = Transaction.withdrawalWithAmount(amount = BigDecimal(amount), desc = descText)
            transactionStorage.insert(storedTransaction)

            clearViews()
            toggleView()
            afterTransactionStored(storedTransaction)
        }
    }

    private var isOpen = false
    private fun toggleView() = with(itemView) {
        val (fromRot, toRot) = if (!isOpen) 0f to 45f else 45f to 0f
        val rotate = rotate(fromRot, toRot)
        vNewTransactionPlaceholderImg.startAnimation(rotate)

        val expandOrCollapseAnim = if (!isOpen) vNewTransactionParent.expandDown() else vNewTransactionParent.collapseUp()
        expandOrCollapseAnim.endAction {
            isOpen = !isOpen

            // View is already open and isOpen flag changed
            val (fromScale, toScale) = if (isOpen) 0f to 1f else 1f to 0f
            vNewTransactionPlaceholderSave.startAnimation(scaleFromMiddle(fromScale, toScale).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        itemView.vNewTransactionPlaceholderSave.visibility = if (isOpen) VISIBLE else INVISIBLE
                        itemView.vNewTransactionPlaceholderSave.setOnClickListener(if (isOpen) saveClickListener else null)
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        itemView.vNewTransactionPlaceholderSave.visibility = if (isOpen) INVISIBLE else VISIBLE
                        itemView.vNewTransactionPlaceholderSave.setOnClickListener(if (isOpen) saveClickListener else null)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            })
        }
        vNewTransactionParent.startAnimation(expandOrCollapseAnim)
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

    private fun NewTransactionPlaceHolderViewHolder.gatSaveParams(): SaveTransactionViewModel {
        val amount = itemView.vNewTransactionAmount.text.toString()
        val descText = itemView.vNewTransactionDescription.text.toString()
        return SaveTransactionViewModel(amount, descText)
    }

    data class SaveTransactionViewModel(val amount: String, val desc: String)
}

