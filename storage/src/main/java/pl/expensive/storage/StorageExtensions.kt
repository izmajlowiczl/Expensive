package pl.expensive.storage

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.math.BigDecimal
import java.util.*

fun <T : Parcelable> Parcel.readParcelable(creator: Parcelable.Creator<T>): T {
    return creator.createFromParcel(this)
}

inline fun <reified T : Parcelable> createParcel(
        crossinline createFromParcel: (Parcel) -> T): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }

fun Transaction.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(date)
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime()
}
fun String.toUUID(): UUID = UUID.fromString(this)
fun String.asBigDecimal(): BigDecimal= BigDecimal(this)

