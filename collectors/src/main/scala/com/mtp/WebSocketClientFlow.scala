package com.mtp

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import argonaut.Argonaut._
import com.mtp.Domain.Response
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WebSocketClientFlow extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val logger = Logger(LoggerFactory.getLogger(getClass.getName))

  val req: WebSocketRequest = WebSocketRequest(uri = "ws://stream.meetup.com/2/rsvps")

  val webSocketFlow: Flow[Message, Message, Future[WebSocketUpgradeResponse]] = Http().webSocketClientFlow(req)

  val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 10, OverflowStrategy.fail)

  val kafkaConnector: KafkaConnector = new KafkaConnector

  val incoming: Sink[Message, Future[Done]] =
    Flow[Message].mapAsync(4) {
      case message: TextMessage.Strict =>
        val msg: Option[Response] = message.asInstanceOf[TextMessage.Strict].getStrictText.decodeOption[Response]
        msg.map {
          logger.info(s"Text Message received.")
          kafkaConnector.sendMsg
        }
        Future.successful(Done)
      case message: TextMessage.Streamed =>
        message.textStream.runWith(Sink.ignore)
      case message: BinaryMessage =>
        message.dataStream.runWith(Sink.ignore)
    }.toMat(Sink.last)(Keep.right)

  val ((completionPromise, _), closed) =
    messageSource
      .viaMat(webSocketFlow)(Keep.both)
      .toMat(incoming)(Keep.both)
      .run()

}
