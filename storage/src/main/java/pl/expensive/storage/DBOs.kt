package pl.expensive.storage

import java.math.BigDecimal
import java.util.*

data class Category(val uuid: UUID = UUID.randomUUID(),
                    val name: String = "",
                    val color: String = "#00000000")

data class Currency(val code: String, val format: String)

data class Transaction(val uuid: UUID,
                       val amount: BigDecimal,
                       val currency: Currency,
                       val date: Long, // time in millis
                       val description: String = "",
                       var category: Category? = null)

fun withdrawal(uuid: UUID = UUID.randomUUID(),
               amount: BigDecimal,
               currency: Currency,
               time: Long = Date().time,
               desc: String = "",
               category: Category? = null) =
        Transaction(uuid, amount.negate(), currency, time, desc, category)

