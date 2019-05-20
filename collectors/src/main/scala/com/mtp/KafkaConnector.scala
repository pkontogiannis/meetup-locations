package com.mtp


import java.util.Properties
import java.util.concurrent.Future

import argonaut.Argonaut._
import com.mtp.Domain.Response
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.concurrent.ExecutionContext.Implicits.global

class KafkaConnector {

  val config: Config = ConfigFactory.load()

  val sinkTopic: JsonField = config.getString("streaming.sink-topic")

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", config.getString("streaming.kafka-bootstrap-servers"))
  properties.setProperty("zookeeper.connect", config.getString("zookeeper.server"))
  properties.setProperty("group.id", "StreamTestGroup")
  properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](properties)

  def sendMsg(response: Response): Future[RecordMetadata] = {
    producer.send(new ProducerRecord[String, String](sinkTopic, response.jencode.toString()))
  }

}
