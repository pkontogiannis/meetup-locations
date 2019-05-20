package com.flink.utils.database

import akka.actor.ActorSystem
import com.flink.persistence.Schema
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class DBAccess(actorSystem: ActorSystem) extends Schema {

  val db: Database = Database.forConfig("postgres")

  private val tables = List(Locations, LocationsCounters, Countries, GroupTopics, GroupsTopicsCounters)

  private val existing: Future[Vector[MTable]] = db.run(MTable.getTables)
  private val f = existing.flatMap(v => {
    val names = v.map(mt => mt.name.name)
    val createIfNotExist = tables.filter(table =>
      !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
    db.run(DBIO.sequence(createIfNotExist))
  })
  Await.result(f, Duration.Inf)
}

