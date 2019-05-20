package com.flink.persistence

import akka.actor.ActorSystem
import com.flink.utils.database.DBAccess
import slick.jdbc.JdbcProfile

trait SlickJdbcProfile {
  val actorSystem: ActorSystem

  lazy val dbAccess = DBAccess(actorSystem)

  lazy val profile: JdbcProfile = slick.jdbc.PostgresProfile

}
