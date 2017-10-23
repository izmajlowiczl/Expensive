package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor

fun listLabels(fromDatabase: Database): List<Label> {
    val cursor = fromDatabase.readableDatabase.simpleQuery(
            tbl_label,
            arrayOf(tbl_label_col_id, tbl_label_col_name))
    val result = cursor.map { labelFrom(it) }
    cursor.close()
    return result
}

fun insertLabel(label: Label, intoDatabase: Database) {
    intoDatabase.writableDatabase.insertOrThrow(tbl_label, null, label.toContentValues())
}

private fun Label.toContentValues(): ContentValues {
    val cv = ContentValues()
    cv.put(tbl_label_col_id, id.toString())
    cv.put(tbl_label_col_name, name)
    return cv
}

private fun labelFrom(cursor: Cursor): Label {
    return Label(
            cursor.uuid(tbl_label_col_id),
            cursor.string(tbl_label_col_name))
}
