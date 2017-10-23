package pl.expensive.storage

import android.database.sqlite.SQLiteDatabase
import pl.expensive.storage._LabelSeeds.FOOD
import pl.expensive.storage._LabelSeeds.HOUSEHOLD
import pl.expensive.storage._LabelSeeds.RENT
import pl.expensive.storage._LabelSeeds.TRANSPORT
import pl.expensive.storage._Seeds.CHF
import pl.expensive.storage._Seeds.CZK
import pl.expensive.storage._Seeds.EUR
import pl.expensive.storage._Seeds.GBP
import pl.expensive.storage._Seeds.PLN
import java.util.*

object _Seeds {
    val EUR = Currency("EUR", "##.##\u00a0\u20ac")
    val GBP = Currency("GBP", "\u00a3##.##")
    val CHF = Currency("CHF", "##.##\u00a0CHF")
    val PLN = Currency("PLN", "##.##\u00a0zł")
    val CZK = Currency("CZK", "##.##\u00a0K\u010d")
}

object _LabelSeeds {
    val FOOD = Label(UUID.fromString("78167ed1-a59a-431e-bfd4-57fb8dbed743"), "Food")
    val RENT = Label(UUID.fromString("edb24ade-2ac1-414e-9281-4d7a3b847d1b"), "Rent")
    val TRANSPORT = Label(UUID.fromString("7c108b80-c954-40bb-8464-0914d28426d5"), "Transportation")
    val HOUSEHOLD = Label(UUID.fromString("e4655b6b-3e75-4ca7-86b5-25d7e4906ada"), "Household")
}

fun applySeeds(db: SQLiteDatabase) {
    storeCurrency(db, EUR)
    storeCurrency(db, GBP)
    storeCurrency(db, CHF)
    storeCurrency(db, PLN)
    storeCurrency(db, CZK)

    storeLabel(db, FOOD)
    storeLabel(db, RENT)
    storeLabel(db, TRANSPORT)
    storeLabel(db, HOUSEHOLD)
}

private fun storeCurrency(db: SQLiteDatabase, currency: Currency) {
    db.execSQL(String.format("INSERT INTO $tbl_currency VALUES('%s', '%s');", currency.code, currency.format))
}

private fun storeLabel(db: SQLiteDatabase, label: Label) {
    db.execSQL(String.format("INSERT INTO $tbl_label VALUES('%s', '%s');", label.id.toString(), label.name))
}
