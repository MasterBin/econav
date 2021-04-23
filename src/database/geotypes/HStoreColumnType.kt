package database.geotypes

import org.geotools.data.postgis.HStore
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgis.Geometry

fun Table.hstore(name : String) : Column<HStore>
        = registerColumn(name, HStoreColumnType())

private class HStoreColumnType() : ColumnType() {
    override fun sqlType() = "hstore"
    override fun valueFromDB(value: Any): Any = value
    override fun notNullValueToDB(value: Any): Any {
        return value
    }
}