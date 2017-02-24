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

    val OTHER = Category(name = "Other", name_res = "cat_other", color = "#00000000")
    val GROCERY = Category(name = "Grocery", name_res = "cat_grocery", color = "#FF61BD4F")
    val FOOD = Category(name = "Food", name_res = "cat_food", color = "#FFF2D600")
    val TRAVEL = Category(name = "Travel", name_res = "cat_travel", color = "#FFFFAB4A")
    val TRANSPORT = Category(name = "Public Transport", name_res = "cat_transport", color = "#EB5A46")
    val CAR = Category(name = "Car", name_res = "cat_car", color = "#C377E0")
    val SPORT = Category(name = "Sport", name_res = "cat_sport", color = "#0079BF")
    val HEALTH = Category(name = "Health", name_res = "cat_health", color = "#0092A9")
    val HOUSE = Category(name = "Household", name_res = "cat_house", color = "#51E898")
    val BILLS = Category(name = "Bills", name_res = "cat_bills", color = "#FF80CE")
    val CLOTHES = Category(name = "Clothes", name_res = "cat_clothes", color = "#4D4D4D")
}
