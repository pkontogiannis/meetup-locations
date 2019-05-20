package com.mtp.service

import akka.actor.{Actor, _}

case class CreateChild(name: String, kafkaConnector: KafkaConnector, method: String)

case class StartReading(kafkaConnector: KafkaConnector, method: String)

class Child extends Actor {

  var name = "No name"

  override def postStop {
    println(s"D'oh! They killed me ($name): ${self.path}")
  }

  def receive: PartialFunction[Any, Unit] = {
    case StartReading(kafkaConnector, method) if method == "writeToDB" =>
      kafkaConnector.writeToDB()
    case StartReading(kafkaConnector, method) if method == "writeToDBTrending" =>
      kafkaConnector.writeToDBTrending()
    case _ => println(s"Child $name got message")
  }
}

class Parent extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case CreateChild(name, kafkaConnector, method) =>
      println(s"Parent about to create Child ($name) ...")
      val child = context.actorOf(Props[Child], name = s"$name")
      child ! StartReading(kafkaConnector, method)
    case _ => println(s"Parent got some other message.")
  }
}
