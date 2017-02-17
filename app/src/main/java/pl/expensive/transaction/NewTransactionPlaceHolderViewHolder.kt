package pl.expensive.transaction

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import kotlinx.android.synthetic.main.view_new_transaction_placeholder_item.view.*
import pl.expensive.*
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import java.math.BigDecimal

class NewTransactionPlaceHolderViewHolder(itemView: View,
                                          val transactionStorage: TransactionStorage,
                                          val afterTransactionStored: (transition: Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {

    var isOpen: Boolean = false

    fun update(viewModel: NewTransactionPlaceHolder) = with(itemView) {
        isOpen = !viewModel.expand
        toggleView()

        vNewTransactionTitleHeader.setOnClickListener { toggleView() }
        vNewTransactionAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // When is expanded and has all mandatory fields filled. Change color of save button
                if (isOpen && vNewTransactionAmount.text.isNotBlank()) {
                    vNewTransactionPlaceholderSave.tint(R.color.ready)
                } else {
                    vNewTransactionPlaceholderSave.tint(R.color.colorTextLight)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
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

    private fun toggleView() = with(itemView) {

        // If hiding clear error state (if any)
        if (!isOpen) {
            vNewTransactionAmount.error = null
        }

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
                vNewTransactionAmount.error = context.getString(R.string.err_mandatory)
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

