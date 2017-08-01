package pl.expensive.tag

import pl.expensive.storage.Tag
import pl.expensive.storage.TagsRepository

fun storeTag(name: CharSequence, repository: TagsRepository, continuation: (storedTag: Tag?, succeed: Boolean) -> Unit) {
    val alreadyExists = repository.list()
            .find { it.name == name.toString() } != null
    if (name.isNullOrEmpty() || alreadyExists) {
        continuation(null, false)
    } else {
        val tag = Tag(name = name.toString())
        continuation(tag, repository.insert(tag))
    }
}

fun filterTags(text: CharSequence?, fromList: List<Tag>): List<Tag> {
    if (text.isNullOrBlank()) {
        return fromList
    } else {
        return fromList.filter { it.name.contains(text.toString(), ignoreCase = true) }
    }
}
