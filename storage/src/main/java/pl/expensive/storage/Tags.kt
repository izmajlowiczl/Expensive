package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor

class TagsRepository(private val cache: TagsCache,
                     private val storage: SQLiteTagsStorage) : TagsStorage {
    override fun list(): List<Tag> {
        val cachedTags = cache.list()
        if (cachedTags.isNotEmpty()) {
            return cachedTags
        }

        return storage.list()
    }

    override fun insert(tag: Tag): Boolean {
        val result = storage.insert(tag)
        if (result) {
            cache.insert(tag)
        } else {
            cache.clear()
        }
        return result

    }
}

interface TagsStorage {
    fun list(): List<Tag>
    fun insert(tag: Tag): Boolean
}

class TagsCache : TagsStorage {
    private val cache: MutableList<Tag> = mutableListOf()

    override fun list(): List<Tag> = cache

    override fun insert(tag: Tag): Boolean {
        return cache.add(tag)
    }

    fun clear() = cache.clear()
}

class SQLiteTagsStorage(private val database: Database) : TagsStorage {
    override fun list(): List<Tag> {
        val cursor = database.readableDatabase.simpleQuery("tbl_tag", arrayOf("uuid", "name"))
        val result = cursor.map { resultToTag(it) }
        cursor.close()
        return result
    }

    private fun resultToTag(cursor: Cursor): Tag =
            Tag(cursor.uuid("uuid"), cursor.string("name"))


    override fun insert(tag: Tag): Boolean {
        val cv = ContentValues()
        cv.put("uuid", tag.uuid.toString())
        cv.put("name", tag.name)

        return database.writableDatabase.insert("tbl_tag", null, cv) != -1L
    }
}
