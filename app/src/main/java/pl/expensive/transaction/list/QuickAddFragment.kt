package pl.expensive.transaction.list

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.view_header_item.*
import kotlinx.android.synthetic.main.view_quick_add_item.*
import pl.expensive.*

class QuickAddFragment : Fragment() {
    interface QuickAddCallbacks {
        fun onQuickAdd(maybeAmountText: Editable?)
    }

    private var callback: QuickAddCallbacks? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_quick_add, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vHeader.text = getString(R.string.quick_add_title)
        vHeaderAmount.visibility = INVISIBLE
        vQuickAddInput.hideKeyboard()
        configureQuickAddView()
    }

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        if (activity is QuickAddCallbacks) {
            callback = activity
        } else {
            throw RuntimeException(activity!!.toString() + " must implement TransactionListCallbacks")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    // In order to not animate submit button each time new character was typed
    // It is set to TRUE after first character and after deleting last character it is restored to FALSE
    private var wasSubmitAnimated = false

    private fun configureQuickAddView() {
        vQuickAddInput.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback!!.onQuickAdd(vQuickAddInput.text)
                vQuickAddInput.hideKeyboard()
                vQuickAddInput.text.clear()
            }
            return@OnEditorActionListener false
        })

        vQuickAddSubmitIcon.setOnClickListener {
            callback!!.onQuickAdd(vQuickAddInput.text)
            vQuickAddInput.hideKeyboard()
            vQuickAddInput.text.clear()
        }

        vQuickAddSubmitIcon.visibility = if (vQuickAddInput.text.isNullOrBlank()) INVISIBLE else VISIBLE
        vQuickAddInput.afterTextChanged1 { text ->
            if (text.isNullOrEmpty()) {
                // Hiding
                wasSubmitAnimated = false
                vQuickAddSubmitIcon.startAnimation(vQuickAddSubmitIcon.scaleFromMiddle(1f, 0f).apply {
                    endAction {
                        vQuickAddSubmitIcon.visibility = INVISIBLE
                    }
                })
            } else {
                // Showing
                if (!wasSubmitAnimated) {
                    wasSubmitAnimated = true
                    vQuickAddSubmitIcon.startAnimation(vQuickAddSubmitIcon.scaleFromMiddle(0f, 1f).apply {
                        endAction {
                            vQuickAddSubmitIcon.visibility = VISIBLE
                        }
                    })
                }
            }
        }
    }
}
