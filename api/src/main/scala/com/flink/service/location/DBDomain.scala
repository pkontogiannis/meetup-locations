package com.flink.service.location

object DBDomain {

  case class Location(id: Int, country: String, state: Option[String], city: String)

  case class LocationCounter(locationId: Int, counter: Int, time: Long)

  case class GroupTopic(id: Long, urlKey: String, topicName: String)

  case class Country(id: Int, name: String)

  case class GroupTopicCounter(countryId: Int, groupTopicId: Long, counter: Int, time: Long)

  case class LocationResult(country: String, state: Option[String], city: String, counter: Int, time: Long)

  case class GroupTopicTrendResult(country: String, groupTopicName: String, groupTopicUrl: String, counter: Int, time: Long)

}
