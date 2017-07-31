package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor

fun listTags(database: Database): List<Tag> {
    val cursor = database.readableDatabase.simpleQuery("tbl_tag", arrayOf("uuid", "name"))
    val result = cursor.map { resultToTag(it) }
    cursor.close()
    return result
}

fun insertTag(tag: Tag, database: Database): Boolean {
    val cv = ContentValues()
    cv.put("uuid", tag.uuid.toString())
    cv.put("name", tag.name)

    return database.writableDatabase.insert("tbl_tag", null, cv) != -1L
}

private fun resultToTag(cursor: Cursor): Tag =
        Tag(cursor.uuid("uuid"), cursor.string("name"))
