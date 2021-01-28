package ru.nk.econav.database

import org.jetbrains.exposed.sql.Table

/**
 * Created by n.samoylov on 18.11.2020
 */
object Users : Table() {
    val id = uuid("id").autoGenerate()
    val userName = varchar("username", 255)
    val dateCreated = long("dateCreated")
    override val primaryKey = PrimaryKey(id)
}