package pl.expensive.month_overview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_month_overview.*
import org.threeten.bp.YearMonth
import pl.expensive.Injector
import pl.expensive.R
import pl.expensive.extra_month_overview_date
import pl.expensive.transaction.list.TransactionListFragment
import pl.expensive.transaction.list.ViewState

class MonthOverviewActivity : AppCompatActivity() {
    private val transactionsModel by lazy { Injector.app().transactionsModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_overview)

        if (intent.hasExtra(extra_month_overview_date)) {
            val ym: YearMonth = intent.getSerializableExtra(extra_month_overview_date) as YearMonth
            transactionsModel.showWalletForMonth(ym, update)
        } else {
            finish()
            return
        }

        vBack.setOnClickListener { finish() }
    }

    private val update: (ViewState) -> Unit = {
        when (it) {
            is ViewState.Wallets -> {
                vMonthOverviewTitle.text = it.title
                transactionsFragment().showTransactions(it.adapterData)
            }
            is ViewState.Empty -> transactionsFragment().showEmpty()
        }
    }

    private fun transactionsFragment(): TransactionListFragment =
            supportFragmentManager.findFragmentById(R.id.fMonthOverviewTransactions) as TransactionListFragment
}
