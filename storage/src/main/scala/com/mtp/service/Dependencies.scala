package com.mtp.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mtp.service.meetup.persistence.PersistenceSQL
import com.mtp.service.meetup.{MeetUpService, MeetUpServiceDefault}
import com.mtp.utils.database.DBAccess

case class Dependencies(
                         meetUpService: MeetUpService,
                       )

object Dependencies {

  def fromConfig(implicit system: ActorSystem, mat: Materializer): Dependencies = {

    val dbAccess = DBAccess(system)

    val locationPersistence = new PersistenceSQL(dbAccess)

    val meetUpService = new MeetUpServiceDefault(locationPersistence)

    Dependencies(meetUpService)

  }

}