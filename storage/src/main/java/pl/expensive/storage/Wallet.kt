package pl.expensive.storage

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Wallet(val uuid: UUID,
                  val name: String,
                  val currency: Currency) : Parcelable {

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString().toUUID(),
            parcelIn.readString(),
            parcelIn.readParcelable(Currency.CREATOR))

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeString(uuid.toString())
            writeString(name)
            writeParcelable(currency, 0)
        }
    }

    override fun describeContents() = 0

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel(::Wallet)
    }
}
