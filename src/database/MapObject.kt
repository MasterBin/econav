package ru.nk.econav.database

import org.jetbrains.exposed.sql.Table
import java.util.*

/**
 * Created by n.samoylov on 12.01.2021
 */
object MapObject : Table("map_objects") {
    val id = long("id").entityId().autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val name = varchar(name = "name", length = 200) //utf8
    val asciiName = varchar(name = "name", length = 200) //ascii
    val alternateNames = varchar(name = "alternatenames", length = 10000)
    val latitude = decimal(name = "latitude", precision = 8, scale = 5)
    val longitude = decimal(name = "longitude", precision = 8, scale = 5)
    val featureClass = varchar(name = "feature_class", length = 1)
    val featureCode = varchar(name = "feature_code", length = 10)
    val countryCode = varchar(name = "country_code", length = 2)
    val countryCodeAlternate = varchar(name = "cc2", length = 200)
    val admin1Code = varchar(name = "admin1_code", length = 20)
    val admin2Code = varchar(name = "admin2_code", length = 80)
    val admin3Code = varchar(name = "admin3_code", length = 20)
    val admin4Code = varchar(name = "admin4_code", length = 20)
    val population = long("population")
    val elevation = integer("elevation")
    val dem = integer("dem")
}