package com.flink.service.location

import com.flink.service.errors.ServiceError
import com.flink.service.location.DBDomain.{GroupTopicTrendResult, LocationResult}

import scala.concurrent.Future

trait LocationService {

  def getTopLocations(limit: Int): Future[Either[ServiceError, List[LocationResult]]]

  def getGroupTopicTrends(country: String, limit: Int): Future[Either[ServiceError, List[GroupTopicTrendResult]]]

}
