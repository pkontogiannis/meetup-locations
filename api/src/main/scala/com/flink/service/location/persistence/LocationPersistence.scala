package com.flink.service.location.persistence

import com.flink.service.errors.ServiceError
import com.flink.service.location.DBDomain.{GroupTopicTrendResult, LocationResult}

import scala.concurrent.Future

trait LocationPersistence {

  def getLocations(limit: Int): Future[Either[ServiceError, List[LocationResult]]]

  def getGroupTopicTrends(country: String, limit: Int): Future[Either[ServiceError, List[GroupTopicTrendResult]]]

}
