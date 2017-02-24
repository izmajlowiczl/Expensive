package pl.expensive.storage

import java.util.*

object _Seeds {

    val EUR = Currency("EUR", "##.##\u00a0\u20ac")
    val GBP = Currency("GBP", "\u00a3##.##")
    val CHF = Currency("CHF", "##.##\u00a0CHF")
    val PLN = Currency("PLN", "##.##\u00a0zł")
    val CZK = Currency("CZK", "##.##\u00a0K\u010d")

    val CASH_ID = UUID.fromString("c2ee3260-94eb-4cc2-8d5c-af38f964fbd5")!!
    val CASH = Wallet(CASH_ID, "Cash", EUR)

    val OTHER = Category(    color = "#00000000")
    val GROCERY = Category( color = "#FF61BD4F")
    val FOOD = Category(    color = "#FFF2D600")
    val TRAVEL = Category(color = "#FFFFAB4A")
    val TRANSPORT = Category(color = "#EB5A46")
    val CAR = Category(color = "#C377E0")
    val SPORT = Category(color = "#0079BF")
    val HEALTH = Category(color = "#0092A9")
    val HOUSE = Category(color = "#51E898")
    val BILLS = Category(color = "#FF80CE")
    val CLOTHES = Category(color = "#4D4D4D")
}
