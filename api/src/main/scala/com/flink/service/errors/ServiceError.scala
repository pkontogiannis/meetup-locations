package com.flink.service.errors

sealed trait ServiceError

object ServiceError {

  case class NotAvailableData() extends ServiceError

  case class ClientServiceError(message: String) extends ServiceError

  val httpErrorMapper: PartialFunction[ServiceError, HttpError] = {
    case NotAvailableData() =>
      new MappingNotFoundErrorHttp {
        override val code: String = "dataNotFound"
        override val message: String = s"There is no available data"
      }
    case ClientServiceError(msg) =>
      new ClientServiceErrorHttp {
        override val code: String = "mappingNotFound"
        override val message: String = s"$msg"
      }
  }
}
