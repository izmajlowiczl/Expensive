package pl.expensive.storage

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert
import java.util.*

internal object DatabaseSchemaTestHelper {

    fun SQLiteDatabase.doesTableExist(tableName: String): Boolean {
        val sql = "SELECT name FROM sqlite_master WHERE type='table';"
        val c = rawQuery(sql, null)
        while (c.moveToNext()) {
            if (c.getString(0) == tableName) return true
            else continue
        }

        return false
    }

    fun getTableColumns(db: SQLiteDatabase, tableName: String): List<String> {
        var c: Cursor? = null
        try {
            c = db.rawQuery("pragma table_info($tableName);", null)

            val columns = ArrayList<String>()
            while (c!!.moveToNext()) {
                columns.add(c.getString(c.getColumnIndex("name")))
            }

            c.close()
            return columns
        } catch (ignored: Exception) {
            return emptyList()
        }

    }

    // Currencies

    fun assertCurrencyCodeStored(db: SQLiteDatabase, code: String) {
        val cursor = db.rawQuery(queryForCurrencyCode(code), null)
        if (cursor == null || !cursor.moveToNext()) {
            Assert.fail("Cannot find currency code")
        }

        assertThat(cursor!!.getString(0))
                .isEqualTo(code)
    }

    fun queryForCurrencyCode(code: String): String {
        return String.format("SELECT code FROM $tbl_currency WHERE code='%s';", code)
    }
}
