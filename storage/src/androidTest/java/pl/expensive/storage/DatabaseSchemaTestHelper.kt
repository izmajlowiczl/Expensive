package pl.expensive.storage

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert
import org.junit.Assert.fail
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

    fun getStoredWalletNames(db: SQLiteDatabase): List<String> {
        val cursor = db.rawQuery(selectNameFrom("tbl_wallet"), null)
        if (cursor == null) {
            fail("Wallet was not stored")
            cursor!!.close()
        }
        val storedWalletNames = ArrayList<String>()
        while (cursor.moveToNext()) {
            storedWalletNames.add(cursor.getString(0))
        }
        return storedWalletNames
    }

    private fun selectNameFrom(table: String): String {
        return String.format("SELECT name FROM %s", table)
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
        return String.format("SELECT code FROM tbl_currency WHERE code='%s';", code)
    }

    // -- Currencies
}
