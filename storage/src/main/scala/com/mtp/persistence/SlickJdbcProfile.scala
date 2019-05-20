package com.mtp.persistence

import akka.actor.ActorSystem
import com.mtp.utils.database.DBAccess
import slick.jdbc.JdbcProfile

trait SlickJdbcProfile {
  val actorSystem: ActorSystem

  lazy val dbAccess = DBAccess(actorSystem)

  lazy val profile: JdbcProfile = slick.jdbc.PostgresProfile

}
