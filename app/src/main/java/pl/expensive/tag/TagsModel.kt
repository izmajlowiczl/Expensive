package pl.expensive.tag

import android.text.Editable
import pl.expensive.storage.Database
import pl.expensive.storage.Tag
import pl.expensive.storage.insertTag
import pl.expensive.storage.listTags

fun storeTag(name: CharSequence, database: Database, continuation: (storedTag: Tag?, succeed: Boolean) -> Unit) {
    val alreadyExists = listTags(database).find { it.name == name.toString() } != null
    if (name.isNullOrEmpty() || alreadyExists) {
        continuation(null, false)
    } else {
        val tag = Tag(name = name.toString())
        val inserted = insertTag(tag, database)
        continuation(tag, inserted)
    }
}

fun filterTags(text: Editable?, database: Database): List<Tag> {
    val allStoredTags = listTags(database)
    if (text.isNullOrBlank()) {
        return allStoredTags
    } else {
        return allStoredTags.filter { it.name == text.toString() }
    }
}
