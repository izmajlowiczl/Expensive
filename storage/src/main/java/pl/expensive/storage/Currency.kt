package pl.expensive.storage

import android.os.Parcel
import android.os.Parcelable

data class Currency(val code: String, val format: String) : Parcelable {

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(code)
        dest.writeString(format)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel(::Currency)
    }
}
