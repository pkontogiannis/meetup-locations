package com.mtp.service.domain

import argonaut.{Argonaut, CodecJson}

object Domain {

  case class ApiLocationResponse(data: List[MeetUpLocation])

  case class MeetUpLocationWindow(endTs: Long, locations: List[MeetUpLocation])

  case class MeetUpLocation(location: String, count: Int, meetUps: List[OutputResponse])

  case class Location(country: String, state: Option[String], city: String)

  case class MeetUpLocationWithDate(location: Location, count: Int, meetUps: List[OutputResponse], endTs: Long)

  case class GroupTopic(urlkey: String, topic_name: String)

  case class MeetUpLocationTrending(country: String, groupTopic: GroupTopic, count: Int, endTs: Long)

  case class MeetUpTopicByCountry(country: String, groupTopic: GroupTopic, count: Int, endTs: Long)

  case class OutputResponse(
                             venue_name: String,
                             lon: Double,
                             lat: Double,
                             rsvp_id: Int,
                             group_city: String,
                             group_country: String,
                             group_state: Option[String],
                           )

  implicit val codecLocation: CodecJson[Location] = Argonaut.casecodec3(Location.apply, Location.unapply)("country", "state", "city")
  implicit val codecGroupTopic: CodecJson[GroupTopic] = Argonaut.casecodec2(GroupTopic.apply, GroupTopic.unapply)("urlkey", "topic_name")
  implicit val codecMeetUpLocationTrending: CodecJson[MeetUpLocationTrending] = Argonaut.casecodec4(MeetUpLocationTrending.apply, MeetUpLocationTrending.unapply)("country", "groupTopic", "count", "endTs")
  implicit val codecMeetUpTopicByCountry: CodecJson[MeetUpTopicByCountry] = Argonaut.casecodec4(MeetUpTopicByCountry.apply, MeetUpTopicByCountry.unapply)("country", "groupTopic", "count", "endTs")
  implicit val codecOutputResponse: CodecJson[OutputResponse] = Argonaut.casecodec7(OutputResponse.apply, OutputResponse.unapply)("venue_name", "lon", "lat", "rsvp_id", "group_city", "group_country", "group_state")
  implicit val codecVenueMeetUpLocation: CodecJson[MeetUpLocation] = Argonaut.casecodec3(MeetUpLocation.apply, MeetUpLocation.unapply)("location", "count", "meetUps")
  implicit val codecVenueMeetUpLocationWindow: CodecJson[MeetUpLocationWindow] = Argonaut.casecodec2(MeetUpLocationWindow.apply, MeetUpLocationWindow.unapply)("endTs", "locations")
  implicit val codecMeetUpLocationWithDate: CodecJson[MeetUpLocationWithDate] = Argonaut.casecodec4(MeetUpLocationWithDate.apply, MeetUpLocationWithDate.unapply)("location", "count", "meetUps", "endTs")
  implicit val codecApiLocationResponse: CodecJson[ApiLocationResponse] = Argonaut.casecodec1(ApiLocationResponse.apply, ApiLocationResponse.unapply)("data")
}
