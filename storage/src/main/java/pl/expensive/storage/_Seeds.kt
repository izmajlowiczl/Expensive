package pl.expensive.storage

import java.util.*

object _Seeds {

    val EUR = Currency("EUR", "##.##\u00a0\u20ac")
    val GBP = Currency("GBP", "\u00a3##.##")
    val CHF = Currency("CHF", "##.##\u00a0CHF")
    val PLN = Currency("PLN", "##.##\u00a0zł")
    val CZK = Currency("CZK", "##.##\u00a0K\u010d")

    val CASH = Wallet(UUID.fromString("c2ee3260-94eb-4cc2-8d5c-af38f964fbd5"), "Cash", EUR)

    val OTHER = Category("Other", "cat_other", "#00000000")
    val GROCERY = Category("Grocery", "cat_grocery", "#FF61BD4F")
    val FOOD = Category("Food", "cat_food", "#FFF2D600")
    val TRAVEL = Category("Travel", "cat_travel", "#FFFFAB4A")
    val TRANSPORT = Category("Public Transport", "cat_transport", "#EB5A46")
    val CAR = Category("Car", "cat_car", "#C377E0")
    val SPORT = Category("Sport", "cat_sport", "#0079BF")
    val HEALTH = Category("Health", "cat_health", "#0092A9")
    val HOUSE = Category("Household", "cat_house", "#51E898")
    val BILLS = Category("Bills", "cat_bills", "#FF80CE")
    val CLOTHES = Category("Clothes", "cat_clothes", "#4D4D4D")
}
