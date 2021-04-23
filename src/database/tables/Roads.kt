package ru.nk.econav.database.tables

import database.geotypes.lineString
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Roads : IntIdTable("ways") {

    val name = text("name")
    val type = text("type")
    val oneway = text("oneway")
    val geometry = lineString("geom", 4326)
    val priority = float("priority").default(404f)

    val wayId = long("way_id")
}


class Road(id : EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Road>(Roads)

    var name by Roads.name
    var oneway by Roads.oneway
    var geometry by Roads.geometry
    var priority by Roads.priority
    var wayId by Roads.wayId
}






