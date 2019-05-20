package com.flink.service.routes

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import com.flink.service.errors._
import com.flink.service.location.LocationService
import io.circe.generic.auto._
import io.circe.syntax._

import scala.util.{Failure, Success}

class MeetUpRoutes(val locationService: LocationService)
  extends Routes {

  val meetUpRoutes: Route =
    meetUp.routes

  object meetUp {

    def completeEither[E <: ServiceError, R: ToEntityMarshaller]
    (statusCode: StatusCode, either: => Either[E, R])(
      implicit mapper: ErrorMapper[E, HttpError]
    ): Route = {
      either match {
        case Left(value) => complete(statusCode, ErrorResponse(code = value.code, message = value.message))
        case Right(value) => complete(statusCode, value)
      }
    }

    implicit val httpErrorMapper: ErrorMapper[ServiceError, HttpError] =
      Routes.buildErrorMapper(ServiceError.httpErrorMapper)

    def routes: Route = logRequestResult("FlinkRoutes") {
      pathPrefix("api" / version)(
        meetUpManagement
      )
    }

    def meetUpManagement: Route =
      topLocations ~ topTrends

    def topLocations: Route = {

      pathPrefix("topLocations") {
        path("snapshot") {
          pathEndOrSingleSlash {
            get {
              parameters('limit ? 20) {
                limit =>
                  onComplete(locationService.getTopLocations(limit)) {
                    case Success(future) =>
                      completeEither(StatusCodes.OK, future)
                    case Failure(_) =>
                      complete(StatusCodes.BadRequest,
                        ErrorResponse(code = "badRequest", message = "There is no available mapping"))
                  }
              }
            }
          }
        }
      }
    }

    def topTrends: Route = {
      pathPrefix("topTrends") {
        pathPrefix(Segment) { country =>
          pathEndOrSingleSlash {
            get {
              parameters('limit ? 20) {
                limit =>
                  onComplete(locationService.getGroupTopicTrends(country, limit)) {
                    case Success(future) =>
                      completeEither(StatusCodes.OK, future)
                    case Failure(_) =>
                      complete(StatusCodes.BadRequest,
                        ErrorResponse(code = "badRequest", message = "There is no available mapping"))
                  }
              }
            }
          }
        }
      }
    }
  }

}



