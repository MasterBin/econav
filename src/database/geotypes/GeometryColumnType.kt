package database.geotypes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgis.Geometry
import org.postgis.PGgeometry

fun Table.geometry(name : String) : Column<Geometry>
        = registerColumn(name, GeometryColumnType())

private class GeometryColumnType : ColumnType() {

    override fun sqlType(): String = "GEOMETRY"

    override fun valueFromDB(value: Any): Any = if (value is PGgeometry) value.geometry else value

    override fun notNullValueToDB(value: Any): Any {
        if (value is Geometry) {
            return PGgeometry(value)
        }
        return value
    }

}