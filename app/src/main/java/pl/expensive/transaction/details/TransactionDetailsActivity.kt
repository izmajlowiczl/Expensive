package pl.expensive.transaction.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.storage.TransactionDbo

class TransactionDetailsActivity : AppCompatActivity(),
        NewTransactionFragment.NewTransactionCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
        Injector.app().inject(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.vNewTransactionContainer, createNewTransactionFragment(intent.extras), "new_transaction_fragment")
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.vNewTransactionContainer, createNewTransactionFragment(intent.extras), "new_transaction_fragment")
                    .commit()

        }
    }

    override fun onNewTransaction(transaction: TransactionDbo) {
        finishWithResult(getString(R.string.new_withdrawal_success_message, transaction.amount.abs()))
    }

    override fun onTransactionEdited(transaction: TransactionDbo) {
        finishWithResult(getString(R.string.withdrawal_edited_success_message, transaction.amount.abs()))
    }

    override fun onCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun finishWithResult(msg: String) {
        val extras = Bundle()
        extras.putString("message", msg)
        setResult(Activity.RESULT_OK, Intent().putExtras(extras))
        finish()
    }
}
