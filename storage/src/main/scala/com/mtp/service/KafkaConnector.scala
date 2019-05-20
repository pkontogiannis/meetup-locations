package com.mtp.service

import java.util.{Collections, Properties}

import argonaut.Argonaut._
import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import com.mtp.service.domain.Domain.{MeetUpLocationTrending, MeetUpLocationWithDate}
import com.mtp.service.meetup.DBDomain._
import com.mtp.service.meetup.MeetUpService
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

class KafkaConnector(meetUpService: MeetUpService) {

  val config: Config = ConfigFactory.load()

  val sourceTopicCount: JsonField = config.getString("streaming.source-topic-count")
  val sourceTopicTrending: JsonField = config.getString("streaming.source-topic-trending")

  val props = new Properties()
  props.put("bootstrap.servers", config.getString("streaming.kafka-bootstrap-servers"))

  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "StreamTestGroup")

  val consumer = new KafkaConsumer[String, String](props)

  def writeToDB(): Unit = {

    consumer.subscribe(Collections.singletonList(sourceTopicCount))

    while (true) {
      val records = consumer.poll(15000)
      for (record <- records.asScala) {
        val rec = record.value.decodeOption[MeetUpLocationWithDate]
        rec.map { obj =>
          val location: Location = Location(country = obj.location.country, state = obj.location.state, city = obj.location.city)
          for {
            loc <- EitherT(meetUpService.createLocation(location))
            locationCounter: LocationCounter = LocationCounter(loc.id.get, obj.count, obj.endTs)
            _ <- EitherT(meetUpService.updateLocationCounter(locationCounter))
          } yield ()
        }
      }
    }
  }


  def writeToDBTrending(): Unit = {

    consumer.subscribe(Collections.singletonList(sourceTopicTrending))

    while (true) {
      val records = consumer.poll(15000)
      for (record <- records.asScala) {
        val rec = record.value.decodeOption[MeetUpLocationTrending]
        rec.map { obj =>
          for {
            country <- EitherT(meetUpService.createCountry(Country(name = obj.country)))
            groupTopic <- EitherT(meetUpService.createGroupTopic(
              GroupTopic(urlKey = obj.groupTopic.urlkey, topicName = obj.groupTopic.topic_name)
            ))
            _ <- EitherT(meetUpService.updateGroupTopicCounter(
              GroupTopicCounter(country.id.get, groupTopic.id.get, obj.count, obj.endTs)
            ))
          } yield ()
        }
      }
    }
  }


}
