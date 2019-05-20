package com.mtp.service.meetup

object DBDomain {

  case class Location(id: Option[Int] = None, country: String, state: Option[String], city: String)

  case class LocationCounter(locationId: Int, counter: Int, time: Long)

  case class GroupTopic(id: Option[Long] = None, urlKey: String, topicName: String)

  case class Country(id: Option[Int] = None, name: String)

  case class GroupTopicCounter(countryId: Int, groupTopicId: Long, counter: Int, time: Long)

}
