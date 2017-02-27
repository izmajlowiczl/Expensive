package pl.expensive.storage

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import java.util.*

class SQLiteBasedCategoryStorage(private val db: Database) : CategoryStorage {
    override fun list(): List<Category> {
        val cursor = db.readableDatabase.query("tbl_category", arrayOf("uuid", "name", "color"), null, null, null, null, null)

        val result = ArrayList<Category>()
        while (cursor.moveToNext()) {
            result.add(Category(cursor.uuid(0), cursor.getString(1), cursor.getString(2)))
        }

        cursor.close()
        return result
    }

    override fun insert(dbo: Category) {
        val cv = ContentValues()
        cv.put("uuid", dbo.uuid.toString())
        cv.put("name", dbo.name)
        cv.put("color", dbo.name)

        try {
            db.writableDatabase.insertOrThrow("tbl_category", null, cv)
        } catch (ex: SQLiteConstraintException) {
            throw IllegalStateException("Trying to store category, which already exist. Category -> " + dbo)
        }
    }
}
