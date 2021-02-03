package ru.nk.econav.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgis.PGgeometry
import org.postgis.Point

object Points : Table("planet_osm_point") {
    val osmId = long("osm_id")
    val access = text("access")
    val way = point("way", 3857)
    override val primaryKey = PrimaryKey(osmId)
}

fun Table.point(name : String, srid : Int = 4326) : Column<Point>
    = registerColumn(name, PointColumnType(srid))

private class PointColumnType(private val srid: Int = 4326) : ColumnType() {

    override fun sqlType() = "GEOMETRY(Point, $srid)"

    override fun valueFromDB(value: Any): Any = if (value is PGgeometry) value.geometry else value
    override fun notNullValueToDB(value: Any): Any {
        if (value is Point) {
            if (value.srid == Point.UNKNOWN_SRID) value.srid = srid
            return PGgeometry(value)
        }
        return value
    }
}


