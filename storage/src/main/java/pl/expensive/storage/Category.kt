package pl.expensive.storage

import java.util.*

data class Category(val uuid: UUID = UUID.randomUUID(),
                    val name: String,
                    val name_res: String = "",
                    val color: String = "#00000000")
