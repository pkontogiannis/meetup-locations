package com.flink

import java.util.Properties

import argonaut.Argonaut._
import com.flink.Codecs._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.serialization.{DeserializationSchema, SerializationSchema}
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.extensions._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.util.Collector
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import scala.collection.JavaConverters._

object GroupByLocation extends App {

  type WindowStreamLocation = WindowedStream[(Location, Response), String, TimeWindow]

  startApp()

  def startApp(): Unit = {

    val config: Config = ConfigFactory.load()

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.enableCheckpointing(1000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", config.getString("streaming.kafka-bootstrap-servers"))
    properties.setProperty("zookeeper.connect", config.getString("zookeeper.server"))
    properties.setProperty("group.id", "StreamTestGroup")

    val sourceTopic = config.getString("streaming.source-topic")

    val sinkTopicCount = config.getString("streaming.sink-topic-count")
    val sinkTopicTrending = config.getString("streaming.sink-topic-trending")

    val adminClient: AdminClient = AdminClient.create(properties)

    adminClient.createTopics(
      List(
        new NewTopic(sinkTopicCount, 1, 1),
        new NewTopic(sinkTopicTrending, 1, 1)
      ).asJava
    )

    val kafkaConsumer = new FlinkKafkaConsumer(sourceTopic, KafkaStringSchema, properties)

    val stream: DataStream[String] = env.addSource(kafkaConsumer)

    countByLocation(config, sinkTopicCount, stream)

    trendingTopics(config, sinkTopicTrending, stream)

    env.execute("FlinkKafkaStreaming")

  }

  private def trendingTopics(config: Config, sinkTopic: String, stream: DataStream[String]): DataStreamSink[String] = {

    val parsedStream: DataStream[(Location, GroupTopic)] = stream
      .mapWith(_.decodeOption[Response])
      .filter(_.isDefined)
      .flatMap { record =>
        record.get.group.group_topics.map(
          topic => (
            Location(record.get.group.group_country, record.get.group.group_state, record.get.group.group_city),
            topic
          )
        )
      }

    val topicsCounterWindow: WindowedStream[(Location, GroupTopic), (String, GroupTopic), TimeWindow] = parsedStream.keyBy(
      tuple => (tuple._1.country, tuple._2)
    ).window(SlidingProcessingTimeWindows.of(
      Time.seconds(config.getInt("streaming.window-size")),
      Time.seconds(config.getInt("streaming.window-interval"))
    ))


    val countLocationsApply: DataStream[MeetUpLocationTrending] = topicsCounterWindow.apply(
      (key: (String, GroupTopic), twindow: TimeWindow, it: Iterable[(Location, GroupTopic)], coll: Collector[MeetUpLocationTrending]) => {
        val meetUpLocationTrending: MeetUpLocationTrending = MeetUpLocationTrending(key._1, key._2, it.size, twindow.getEnd)
        coll.collect(meetUpLocationTrending)
      }
    )

    val tmp: DataStream[String] = countLocationsApply.map(
      output =>
        output.jencode.toString()
    )

    val kafkaProducer = new FlinkKafkaProducer[String](config.getString("streaming.kafka-bootstrap-servers"), sinkTopic, KafkaStringSchema)

    tmp.addSink(kafkaProducer)

  }

  private def countByLocation(config: Config, sinkTopic: String, stream: DataStream[String]): DataStreamSink[String] = {

    val parsedStream: DataStream[(Location, Response)] = stream
      .mapWith(_.decodeOption[Response])
      .filter(_.isDefined)
      .map { record =>
        (
          Location(record.get.group.group_country, record.get.group.group_state, record.get.group.group_city),
          record.get
        )
      }

    val countLocationsWindow: WindowedStream[(Location, Response), Location, TimeWindow] = parsedStream
      .keyBy(tuple => tuple._1)
      .window(SlidingProcessingTimeWindows.of(
        Time.seconds(config.getInt("streaming.window-size")),
        Time.seconds(config.getInt("streaming.window-interval"))
      ))

    val countLocationsApply: DataStream[MeetUpLocationWithDate] = countLocationsWindow.apply(
      (key: Location, twindow: TimeWindow, it: Iterable[(Location, Response)], coll: Collector[MeetUpLocationWithDate]) => {
        val meetUpLocationWithDate = MeetUpLocationWithDate(key, it.size, it.map(el => OutputResponse.builder(el._2)).toList.distinct, twindow.getEnd)
        coll.collect(meetUpLocationWithDate)
      }
    )

    val tmp: DataStream[String] = countLocationsApply.map(
      output =>
        output.jencode.toString()
    )

    val kafkaProducer = new FlinkKafkaProducer[String](config.getString("streaming.kafka-bootstrap-servers"), sinkTopic, KafkaStringSchema)

    tmp.addSink(kafkaProducer)
  }

  class CountFunction extends ProcessWindowFunction[(String, Response), MeetUpLocationWindow, String, TimeWindow] {

    override def process(key: String,
                         context: Context,
                         elements: Iterable[(String, Response)],
                         out: Collector[MeetUpLocationWindow]
                        ): Unit = {

      val count: Map[String, Iterable[(String, Response)]] = elements.groupBy(_._1)

      val locAndCount: List[MeetUpLocation] = count.toList.map(tmp => {
        val location: String = tmp._1
        val meetUpList: Iterable[(String, Response)] = tmp._2

        MeetUpLocation(
          location, tmp._2.size,
          meetUpList.map(
            tmp => OutputResponse.builder(tmp._2)
          ).toList
        )
      })

      val windowEnd = context.window.getEnd

      out.collect(MeetUpLocationWindow(windowEnd, locAndCount))
    }
  }

  object KafkaStringSchema extends SerializationSchema[String] with DeserializationSchema[String] {

    import org.apache.flink.api.common.typeinfo.TypeInformation
    import org.apache.flink.api.java.typeutils.TypeExtractor

    override def serialize(t: String): Array[Byte] = t.getBytes("UTF-8")

    override def isEndOfStream(t: String) = false

    override def deserialize(bytes: Array[Byte]) = new String(bytes, "UTF-8")

    override def getProducedType: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
  }

}