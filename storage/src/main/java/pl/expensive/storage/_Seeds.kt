package pl.expensive.storage

import java.util.*

object _Seeds {

    val EUR = Currency("EUR", "##.##\u00a0\u20ac")
    val GBP = Currency("GBP", "\u00a3##.##")
    val CHF = Currency("CHF", "##.##\u00a0CHF")
    val PLN = Currency("PLN", "##.##\u00a0zł")
    val CZK = Currency("CZK", "##.##\u00a0K\u010d")

    val CASH = Wallet(UUID.fromString("c2ee3260-94eb-4cc2-8d5c-af38f964fbd5"), "Cash", EUR)

    val OTHER = Category("Other", "cat_other")
    val GROCERY = Category("Grocery", "cat_grocery")
    val FOOD = Category("Food", "cat_food")
    val TRAVEL = Category("Travel", "cat_travel")
    val TRANSPORT = Category("Public Transport", "cat_transport")
    val CAR = Category("Car", "cat_car")
    val SPORT = Category("Sport", "cat_sport")
    val HEALTH = Category("Health", "cat_health")
    val HOUSE = Category("Household", "cat_house")
    val BILLS = Category("Bills", "cat_bills")
    val CLOTHES = Category("Clothes", "cat_clothes")
}
