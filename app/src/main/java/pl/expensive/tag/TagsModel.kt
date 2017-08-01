package pl.expensive.tag

import pl.expensive.storage.Tag
import pl.expensive.storage.TagsRepository

/* Inline */fun storeTag(tag: Tag, repository: TagsRepository, continuation: (storedTag: Tag?, succeed: Boolean) -> Unit) {
    if (tag.name.isNullOrEmpty()) {
        continuation(null, false)
    } else {
        val alreadyExists = repository.list()
                .find { it.name == tag.name } != null
        if (alreadyExists) {
            continuation(null, false)
        } else {
            continuation(tag, repository.insert(tag))
        }
    }
}

fun filterTags(text: CharSequence?, fromList: List<Tag>): List<Tag> {
    if (text.isNullOrBlank()) {
        return fromList
    } else {
        return fromList.filter { it.name.contains(text.toString(), ignoreCase = true) }
    }
}
