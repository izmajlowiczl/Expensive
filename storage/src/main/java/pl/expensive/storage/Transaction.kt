package pl.expensive.storage

import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import java.util.*

fun withdrawal(uuid: UUID = UUID.randomUUID(),
               amount: BigDecimal,
               currency: Currency,
               time: Long = Date().time,
               desc: String = "",
               category: Category? = null) =
        Transaction(uuid, amount.negate(), currency, time, desc, category)

data class Transaction(val uuid: UUID,
                       val amount: BigDecimal,
                       val currency: Currency,
                       val date: Long, // time in millis
                       val description: String,
                       var category: Category? = null) : Parcelable {

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString().toUUID(),
            parcelIn.readString().asBigDecimal(),
            parcelIn.readParcelable(Currency.CREATOR)!!,
            parcelIn.readLong(),
            parcelIn.readString(),
            parcelIn.readParcelable(Category.CREATOR))

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeString(uuid.toString())
            writeString(amount.toString())
            writeParcelable(currency, 0)
            writeLong(date)
            writeString(description)
            writeParcelable(category, 0)
        }
    }

    override fun describeContents() = 0

    // TODO: Maybe make separate functions instead of companion object for factory methods?
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel(::Transaction)
    }
}
