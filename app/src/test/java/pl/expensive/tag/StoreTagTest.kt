package pl.expensive.tag

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.expensive.storage.Tag
import pl.expensive.storage.TagsRepository

class StoreTagTest {
    val horse = Tag(name = "Horse")
    val carrot = Tag(name = "Carrot")

    val emptyContinuation: (Tag?, Boolean) -> Unit = { _, _ -> }

    @Test fun storeNonExistingTag() {
        val repository = mock<TagsRepository> {
            on { it.list() } doReturn listOf(horse, carrot)
        }

        val tag = Tag(name = "Parrot")
        storeTag(tag, repository, emptyContinuation)

        verify(repository).insert(tag)
    }

    @Test fun canNotStoreTagWithoutName() {
        val repository = mock<TagsRepository> { on { it.list() } doReturn listOf<Tag>() }

        val tag = Tag(name = "")
        storeTag(tag, repository, emptyContinuation)

        verify(repository, times(0)).insert(tag)

    }

    @Test fun canNotStoreDuplicate() {
        val repository = mock<TagsRepository> {
            on { it.list() } doReturn listOf(horse, carrot)
        }

        val existingTag = Tag(name = "Horse")
        storeTag(existingTag, repository, emptyContinuation)

        verify(repository, times(0)).insert(existingTag)

    }
}
