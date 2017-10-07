package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN

object _Seeds {
    val EUR = Currency("EUR", "##.##\u00a0\u20ac")
    val GBP = Currency("GBP", "\u00a3##.##")
    val CHF = Currency("CHF", "##.##\u00a0CHF")
    val PLN = Currency("PLN", "##.##\u00a0zł")
    val CZK = Currency("CZK", "##.##\u00a0K\u010d")
}

fun applySeeds(db: SQLiteDatabase) {
    // Currencies
    storeCurrency(db, EUR)
    storeCurrency(db, GBP)
    storeCurrency(db, CHF)
    storeCurrency(db, PLN)
    storeCurrency(db, CZK)
}

private fun storeCurrency(db: SQLiteDatabase, currency: Currency) {
    db.execSQL(String.format("INSERT INTO $tbl_currency VALUES('%s', '%s');", currency.code, currency.format))
}
