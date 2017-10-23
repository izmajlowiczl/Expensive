package pl.expensive.storage

import java.math.BigDecimal
import java.util.*

private const val col_uuid = "uuid"
typealias Id = UUID

//region Transaction
const val tbl_label = "tbl_label"
const val tbl_label_col_id = col_uuid
const val tbl_label_col_name = "name"

data class Label(val id: Id, val name: String)
//endregion

//region Currency
const val tbl_currency = "tbl_currency"
const val tbl_currency_col_code = "code"
const val tbl_currency_col_format = "format"

data class Currency(val code: String, val format: String)
//endregion

//region Transaction
const val tbl_transaction = "tbl_transaction"
const val tbl_transaction_col_id = col_uuid
const val tbl_transaction_col_amount = "amount"
const val tbl_transaction_col_currency = "currency"
const val tbl_transaction_col_date = "date"
const val tbl_transaction_col_description = "description"

data class Transaction(val uuid: UUID,
                       val amount: BigDecimal,
                       val currency: Currency,
                       val date: Long, // time in millis
                       val description: String)

fun withdrawal(uuid: UUID = UUID.randomUUID(),
               amount: BigDecimal,
               currency: Currency,
               time: Long = Date().time,
               desc: String = "") =
        Transaction(uuid, amount.negate(), currency, time, desc)

fun String.asBigDecimalWithdrawal(): BigDecimal = BigDecimal(this).negate()
//endregion
