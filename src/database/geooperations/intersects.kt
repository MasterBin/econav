package ru.nk.econav.database.geooperations

import org.jetbrains.exposed.sql.*
import org.postgis.Geometry


fun ExpressionWithColumnType<*>.intersects(geometry: Geometry): Op<Boolean> =
    IntersectsOp(this, geometry)

private class IntersectsOp(val expr1: Expression<*>, val geom: Geometry) : Op<Boolean>() {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(
            "ST_Intersects(", expr1, ",'$geom'::geometry)"
        )
    }
}