package pl.expensive.storage

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert
import java.util.*

internal object DatabaseSchemaTestHelper {

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


    //region Labels
    fun assertLabelStored(db: SQLiteDatabase, label: Label) {
        val cursor = db.rawQuery(queryForLabel(label), null)
        if (cursor == null || !cursor.moveToNext()) {
            org.junit.Assert.fail("Cannot find label")
        }

        assertThat(cursor.uuid("uuid")).isEqualTo(label.id)
        assertThat(cursor.string("name")).isEqualTo(label.name)
    }

    fun queryForLabel(label: Label): String {
        return String.format("SELECT uuid, name FROM tbl_label WHERE uuid='%s' AND name='%s';", label.id.toString(), label.name)
    }
    //endregion
}
