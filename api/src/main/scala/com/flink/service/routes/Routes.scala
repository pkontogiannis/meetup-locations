package com.flink.service.routes

import akka.http.scaladsl.server.{Directives, Route}
import com.flink.config.Version
import com.flink.service.Dependencies
import com.flink.service.errors._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

trait Routes extends Version
  with Directives with FailFastCirceSupport

object Routes extends Directives {

  def buildRoutes(dependencies: Dependencies): Route =
    new MeetUpRoutes(dependencies.locationService).meetUpRoutes

  def buildErrorMapper(serviceErrorMapper: PartialFunction[ServiceError, HttpError]): ErrorMapper[ServiceError, HttpError] =
    (e: ServiceError) =>
      serviceErrorMapper
        .applyOrElse(e, (_: ServiceError) => InternalErrorHttp("Unexpected error"))

}
