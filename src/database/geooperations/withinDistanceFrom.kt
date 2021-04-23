package database.geooperations

import org.jetbrains.exposed.sql.*
import org.postgis.Geometry
import org.postgis.Point

fun ExpressionWithColumnType<*>.withinDistanceFrom(geometry: Geometry, distance: Double): Op<Boolean> =
    WithinDistanceFromLineOp(this, geometry, distance)

private class WithinDistanceFromLineOp(val expr1: Expression<*>, val line: Geometry, val radiusM: Double) : Op<Boolean>() {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(
            "ST_DWithin(ST_Transform(",
            expr1,
            ",3857),ST_Transform('$line'::geometry, 3857), ${radiusM})"
        )
    }
}