package pl.expensive.storage

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import java.math.BigDecimal
import java.util.*

// TODO MAke parcelable
data class Transaction(val uuid: UUID,
                       val wallet: UUID,
                       val amount: BigDecimal,
                       val currency: Currency,
                       val date: Long, // time in millis
                       val description: String,
                       var category: Category? = null) {

    fun toLocalDateTime(): LocalDateTime {
        return Instant.ofEpochMilli(date)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()
    }

    companion object {
        fun withdrawalWithAmount(uuid: UUID = UUID.randomUUID(),
                                 wallet: UUID = _Seeds.CASH_ID,
                                 amount: BigDecimal,
                                 currency: Currency,
                                 time: Long = Date().time,
                                 desc: String = "",
                                 category: Category? = null): Transaction {
            return Transaction(uuid, wallet, amount.negate(), currency, time, desc, category)
        }

        fun create(uuid: UUID, wallet: UUID, amount: BigDecimal, currency: Currency, date: LocalDateTime, desc: String): Transaction {
            return Transaction(uuid, wallet, amount, currency, toMillisUTC(date), desc)
        }

        fun create(uuid: UUID, wallet: UUID, amount: BigDecimal, currency: Currency, time: Long, desc: String): Transaction {
            return Transaction(uuid, wallet, amount, currency, time, desc)
        }

        fun withdrawal(wallet: UUID, amount: BigDecimal, currency: Currency, desc: String): Transaction {
            return create(UUID.randomUUID(), wallet, amount.negate(), currency, toMillisUTC(LocalDateTime.now()), desc)
        }

        fun deposit(wallet: UUID, amount: BigDecimal, currency: Currency, desc: String): Transaction {
            return create(UUID.randomUUID(), wallet, amount, currency, toMillisUTC(LocalDateTime.now()), desc)
        }

        private fun toMillisUTC(date: LocalDateTime): Long {
            return date.toInstant(ZoneOffset.UTC).toEpochMilli()
        }
    }
}
