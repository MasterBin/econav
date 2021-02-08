package ru.nk.econav.util

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.Statement
import org.jetbrains.exposed.sql.statements.StatementType
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.PreparedStatement
import java.sql.ResultSet

fun <T:Any> String.execAndMap(transform : (ResultSet) -> T) : List<T> {
    val result = arrayListOf<T>()
    TransactionManager.current().execCTE(this) { rs ->
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}

fun <T:Any> Transaction.execCTE(stmt: String, transform: (ResultSet) -> T): T? {
    if (stmt.isEmpty()) return null

    val type = StatementType.SELECT

    return exec(object : Statement<T>(type, emptyList()) {
        override fun PreparedStatementApi.executeInternal(transaction: Transaction): T? {
            executeQuery()
            return resultSet?.let {
                try {
                    transform(it)
                } finally {
                    it.close()
                }
            }
        }

        override fun prepareSQL(transaction: Transaction): String = stmt

        override fun arguments(): Iterable<Iterable<Pair<ColumnType, Any?>>> = emptyList()
    })
}