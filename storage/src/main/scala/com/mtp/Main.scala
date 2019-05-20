package com.mtp

import akka.actor._
import com.mtp.service.{CreateChild, Dependencies, KafkaConnector, Parent}
import com.mtp.utils.server.Server

object Main extends App with Server {

  def startApplication(): Unit = {

    val dependencies: Dependencies = Dependencies.fromConfig

    val parent = system.actorOf(Props[Parent], name = "Parent")

    // send messages to Parent to create to child actors
    parent ! CreateChild("Locations",
      new KafkaConnector(dependencies.meetUpService), "writeToDB")
    parent ! CreateChild("GroupTopics",
      new KafkaConnector(dependencies.meetUpService), "writeToDBTrending")

  }

  startApplication()

}
