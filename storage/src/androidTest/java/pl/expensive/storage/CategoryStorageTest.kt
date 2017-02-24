package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryStorageTest {
    private lateinit var storage: CategoryStorage
    private lateinit var db: SQLiteDatabase

    @Before
    fun setUp() {
        val inMemDatabase = Injector.provideDatabase()
        db = inMemDatabase.writableDatabase
        storage = SQLiteBasedCategoryStorage(inMemDatabase)
    }

    @Test
    fun storeCategory() {
        val category = Category(name = "FAKE", color = "#FFFFFF")

        storage.insert(category)

        assertThat(storage.list().filter { it.name == "FAKE" })
                .isNotEmpty()
    }

    @Test
    fun storeCategoryWithoutTranslation() {
        val category = Category(name = "FAKE")

        storage.insert(category)

        assertThat(storage.list().filter { it.name == "FAKE" })
                .isNotEmpty()
    }

}
