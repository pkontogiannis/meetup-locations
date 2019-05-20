package com.flink.service.location

import com.flink.service.errors.ServiceError
import com.flink.service.errors.ServiceError.NotAvailableData
import com.flink.service.location.DBDomain.{GroupTopicTrendResult, LocationResult}
import com.flink.service.location.persistence.LocationPersistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LocationServiceDefault(val locationPersistence: LocationPersistence) extends LocationService {

  override def getTopLocations(limit: Int): Future[Either[ServiceError, List[LocationResult]]] = {
    locationPersistence.getLocations(limit).map {
      case Right(loc) =>
        Right(loc)
      case Left(_) => Left(NotAvailableData())
    }
  }

  override def getGroupTopicTrends(country: String, limit: Int): Future[Either[ServiceError, List[GroupTopicTrendResult]]] = {
    locationPersistence.getGroupTopicTrends(country, limit).map {
      case Right(loc) =>
        Right(loc)
      case Left(_) => Left(NotAvailableData())
    }
  }


}
