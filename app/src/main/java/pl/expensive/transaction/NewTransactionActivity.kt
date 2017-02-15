package pl.expensive.transaction

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_transaction.*
import org.jetbrains.anko.toast
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.formatValue
import pl.expensive.hideKeyboard
import pl.expensive.storage.Transaction
import pl.expensive.storage.TransactionStorage
import pl.expensive.storage._Seeds
import java.math.BigDecimal

class NewTransactionActivity : AppCompatActivity() {
    private val transactionStorage: TransactionStorage by lazy(mode = LazyThreadSafetyMode.NONE) {
        Injector.app().transactions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)
        Injector.app().inject(this)

        vCreateTransaction.setOnClickListener {
            if (validate()) {
                val amountText = vCreateTransactionAmount.text.toString()
                val amount = BigDecimal(amountText)
                val descText = vCreateTransactionDescription.text.toString()

                transactionStorage.insert(Transaction.withdrawalWithAmount(amount = amount, desc = descText))

                clearView()
                toast("Transaction for ${_Seeds.EUR.formatValue(money = amount)} created!")
                finish()
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        val amountText = vCreateTransactionAmount.text.toString()
        if (amountText.isNullOrEmpty()) {
            vCreateTransactionAmount.error = "Mandatory"
            isValid = false
        } else {
            vCreateTransactionAmount.error = null
        }
        return isValid
    }

    private fun clearView() {
        vCreateTransactionAmount.text.clear()
        vCreateTransactionDescription.text.clear()
        vCreateTransactionAmount.hideKeyboard()
    }
}
