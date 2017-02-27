package pl.expensive.storage

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Category(val uuid: UUID = UUID.randomUUID(),
                    val name: String = "",
                    val color: String = "#00000000") : Parcelable {

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString().toUUID(),
            parcelIn.readString(),
            parcelIn.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uuid.toString())
        dest.writeString(name)
        dest.writeString(color)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel(::Category)
    }
}

