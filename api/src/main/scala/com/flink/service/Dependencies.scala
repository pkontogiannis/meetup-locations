package com.flink.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.flink.config.Configuration
import com.flink.service.location.persistence.LocationPersistenceSQL
import com.flink.service.location.{LocationService, LocationServiceDefault}
import com.flink.utils.database.DBAccess
import com.typesafe.config.{Config, ConfigFactory}

case class Dependencies(
                         locationService: LocationService,
                       )

object Dependencies {

  private val config: Config = ConfigFactory.load()

  def fromConfig(configuration: Configuration)(implicit system: ActorSystem, mat: Materializer): Dependencies = {

    val dbAccess = DBAccess(system)

    val locationPersistence = new LocationPersistenceSQL(dbAccess)

    val locationService = new LocationServiceDefault(locationPersistence)

    Dependencies(locationService)

  }
}