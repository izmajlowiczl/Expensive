package pl.expensive.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import java.util.*

class SQLiteBasedWalletsStorage(private val database: Database) : WalletsStorage {

    override fun list(): List<Wallet> {
        val readableDatabase = database.readableDatabase
        val columns = arrayOf("w.uuid", "w.name", "c.code ", "c.format")
        val cursor = readableDatabase.query("tbl_wallet w, " + " tbl_currency c", columns, "w.currency=c.code", null, null, null, null)

        val wallets = ArrayList<Wallet>()
        while (cursor.moveToNext()) {
            wallets.add(fromCursor(cursor))
        }

        cursor.close()
        return wallets
    }

    override fun insert(wallet: Wallet) {
        val writableDatabase = database.writableDatabase
        try {
            writableDatabase.insertOrThrow("tbl_wallet", null, toContentValues(wallet))
        } catch (ex: SQLiteConstraintException) {
            throw IllegalStateException("Trying to store wallet, which already exist. Wallet -> " + wallet)
        }

    }

    private fun fromCursor(cursor: Cursor): Wallet {
        val currency = Currency(cursor.getString(2), cursor.getString(3))
        return Wallet(
                cursor.uuid(0),
                cursor.getString(1),
                currency)
    }

    private fun toContentValues(wallet: Wallet): ContentValues {
        val cv = ContentValues()
        cv.put("uuid", wallet.uuid.toString())
        cv.put("name", wallet.name)
        cv.put("currency", wallet.currency.code)
        return cv
    }
}
