package database.geooperations

import org.jetbrains.exposed.sql.*
import org.postgis.PGbox2d

infix fun ExpressionWithColumnType<*>.within(box: PGbox2d) : Op<Boolean>
        = WithinOp(this, box)

private class WithinOp(val expr1: Expression<*>, val box: PGbox2d) : Op<Boolean>() {


    /** Appends the SQL representation of this expression to the specified [queryBuilder]. */
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(
            expr1, " && ST_MakeEnvelope(${box.llb.x}, ${box.llb.y}, ${box.urt.x}, ${box.urt.y}, 4326)"
        )
    }
}