package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import kotlinx.android.synthetic.main.view_new_transaction_placeholder_item.view.*
import pl.expensive.collapseUp
import pl.expensive.endAction
import pl.expensive.expandDown
import pl.expensive.hideKeyboard
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import java.math.BigDecimal

class NewTransactionPlaceHolderViewHolder(itemView: View,
                                          val transactionStorage: TransactionStorage,
                                          val afterTransactionStored: (transition: Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update() = with(itemView) {
        vNewTransactionTitleHeader.setOnClickListener { toggleView() }
    }

    private var isOpen = false
    private fun toggleView() {
        with(itemView) {
            vNewTransactionTitleHeader.isClickable = false
            val expandOrCollapseAnim = if (!isOpen) vNewTransactionParent.expandDown() else vNewTransactionParent.collapseUp()
            expandOrCollapseAnim.endAction {
                isOpen = !isOpen
                vNewTransactionTitleHeader.isClickable = true

                // View is already open and isOpen flag changed
                val (from, to) = if (isOpen) 0f to 1f else 1f to 0f
                val scale = ScaleAnimation(from, to, from, to, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f).apply {
                    duration = 200
                    fillAfter = true
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation?) {
                            vNewTransactionPlaceholderSave.visibility = if (isOpen) INVISIBLE else VISIBLE
                            vNewTransactionPlaceholderSave.setOnClickListener {
                                if (validate()) {
                                    val amount = BigDecimal(vNewTransactionAmount.text.toString())
                                    val descText = vNewTransactionDescription.text.toString()

                                    val storedTransaction = Transaction.withdrawalWithAmount(amount = amount, desc = descText)
                                    transactionStorage.insert(storedTransaction)

                                    clearViews()
                                    toggleView()

                                    afterTransactionStored(storedTransaction)
                                }
                            }
                        }

                        override fun onAnimationStart(animation: Animation?) {
                            vNewTransactionPlaceholderSave.visibility = if (isOpen) VISIBLE else INVISIBLE
                            // Because view is INVISIBLE, listener is removed to not be performed on not visible view
                            vNewTransactionPlaceholderSave.setOnClickListener(null)
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }
                    })

                }
                vNewTransactionPlaceholderSave.startAnimation(scale)
            }

            vNewTransactionPlaceholderImg.animate().rotationBy(45f).start()
            vNewTransactionParent.startAnimation(expandOrCollapseAnim)
        }
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

