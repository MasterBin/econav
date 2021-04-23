package database.geotypes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgis.LineString
import org.postgis.PGgeometry


fun Table.lineString(name: String, srid: Int = 4326): Column<LineString> =
    registerColumn(name, LineStringColumnType(srid))

internal class LineStringColumnType(val srid: Int = 4326) : ColumnType() {
    override fun sqlType() = "GEOMETRY(LineString, $srid)"
    override fun valueFromDB(value: Any) = if (value is PGgeometry) value.geometry else value
    override fun notNullValueToDB(value: Any): Any {
        if (value is LineString) {
            if (value.srid == LineString.UNKNOWN_SRID) value.srid = srid
            return PGgeometry(value)
        }
        return value
    }
}