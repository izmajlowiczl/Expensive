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

    /**
     * Whether expandable view is open or not
     */
    var isCurrentlyOpen: Boolean = false

    fun update(viewModel: NewTransactionPlaceHolder) = with(itemView) {
        isCurrentlyOpen = !viewModel.shouldExpand
        toggleView()

        vNewTransactionTitleHeader.setOnClickListener { toggleView() }
        vNewTransactionAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // When is expanded and has all mandatory fields filled. Change color of save button
                if (isCurrentlyOpen && vNewTransactionAmount.text.isNotBlank()) {
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
        if (!isCurrentlyOpen) {
            vNewTransactionAmount.error = null
        }

        val (fromRot, toRot) = if (!isCurrentlyOpen) 0f to 45f else 45f to 0f
        vNewTransactionPlaceholderImg.startAnimation(rotate(fromRot, toRot))

        vNewTransactionParent.startAnimation(
                if (!isCurrentlyOpen) {
                    vNewTransactionParent.expandDown()
                } else {
                    vNewTransactionParent.collapseUp()
                }.apply {
                    endAction {
                        isCurrentlyOpen = !isCurrentlyOpen

                        // View is already open and isCurrentlyOpen flag changed
                        val (fromScale, toScale) = if (isCurrentlyOpen) 0f to 1f else 1f to 0f
                        vNewTransactionPlaceholderSave.startAnimation(scaleFromMiddle(fromScale, toScale).apply {
                            setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    if (isCurrentlyOpen) {
                                        itemView.vNewTransactionPlaceholderSave.visibility = VISIBLE
                                        itemView.vNewTransactionPlaceholderSave.setOnClickListener(saveClickListener)
                                    } else {
                                        itemView.vNewTransactionPlaceholderSave.visibility = INVISIBLE
                                        itemView.vNewTransactionPlaceholderSave.setOnClickListener(null)
                                    }
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                    if (isCurrentlyOpen) {
                                        itemView.vNewTransactionPlaceholderSave.visibility = INVISIBLE
                                        itemView.vNewTransactionPlaceholderSave.setOnClickListener(null)
                                    } else {
                                        itemView.vNewTransactionPlaceholderSave.visibility = VISIBLE
                                        itemView.vNewTransactionPlaceholderSave.setOnClickListener(saveClickListener)
                                    }
                                }

                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                        })
                    }
                })
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

