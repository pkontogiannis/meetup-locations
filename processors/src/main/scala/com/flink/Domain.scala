package com.flink

import argonaut.{Argonaut, CodecJson}

case class OutputResponse(
                           venue_name: String,
                           lon: Double,
                           lat: Double,
                           rsvp_id: Int,
                           group_city: String,
                           group_country: String,
                           group_state: Option[String],
                         )

object OutputResponse {
  def builder(response: Response): OutputResponse = {
    OutputResponse(
      venue_name = response.venue.venue_name,
      lon = response.venue.lon,
      lat = response.venue.lat,
      rsvp_id = response.rsvp_id,
      group_city = response.group.group_city,
      group_country = response.group.group_country,
      group_state = response.group.group_state
    )
  }
}

case class Venue(
                  venue_name: String,
                  lon: Double,
                  lat: Double,
                  venue_id: Int
                )

case class Member(
                   member_id: Int,
                   photo: String,
                   member_name: String
                 )

case class Event(
                  event_name: String,
                  event_id: String,
                  time: Int,
                  event_url: String
                )

case class GroupTopic(
                       urlkey: String,
                       topic_name: String
                     )

case class Group(
                  group_topics: List[GroupTopic],
                  group_city: String,
                  group_country: String,
                  group_id: Int,
                  group_name: String,
                  group_lon: Double,
                  group_urlname: String,
                  group_state: Option[String],
                  group_lat: Double
                )

case class Location(country: String, state: Option[String], city: String) {
  def key: String = {
    s"$country, $city"
  }
}

case class Response(
                     venue: Venue,
                     visibility: String,
                     response: String,
                     guests: Int,
                     member: Member,
                     rsvp_id: Int,
                     mtime: Int,
                     event: Event,
                     group: Group
                   ) {
  def equalLocation(res: Response): Boolean = {
    if (res.group.group_country == this.group.group_country
      && res.group.group_city == this.group.group_city
      && res.group.group_state == this.group.group_state)
      true
    else
      false
  }
}

case class MeetUpLocationWindow(endTs: Long, locations: List[MeetUpLocation])

case class MeetUpLocation(location: String, count: Int, meetUps: List[OutputResponse])

case class MeetUpLocationWithDate(location: Location, count: Int, meetUps: List[OutputResponse], endTs: Long)

case class MeetUpLocationTrending(country: String, groupTopic: GroupTopic, count: Int, endTs: Long)

object Codecs {
  implicit val codecVenue: CodecJson[Venue] = Argonaut.casecodec4(Venue.apply, Venue.unapply)("venue_name", "lon", "lat", "venue_id")
  implicit val codecMember: CodecJson[Member] = Argonaut.casecodec3(Member.apply, Member.unapply)("member_id", "photo", "member_name")
  implicit val codecEvent: CodecJson[Event] = Argonaut.casecodec4(Event.apply, Event.unapply)("event_name", "event_id", "time", "event_url")
  implicit val codecGroupTopic: CodecJson[GroupTopic] = Argonaut.casecodec2(GroupTopic.apply, GroupTopic.unapply)("urlkey", "topic_name")
  implicit val codecGroup: CodecJson[Group] = Argonaut.casecodec9(Group.apply, Group.unapply)("group_topics", "group_city", "group_country", "group_id", "group_name", "group_lon", "group_urlname", "group_state", "group_lat")
  implicit val codec: CodecJson[Response] = Argonaut.casecodec9(Response.apply, Response.unapply)("venue", "visibility", "response", "guests", "member", "rsvp_id", "mtime", "event", "group")
  implicit val codecLocation: CodecJson[Location] = Argonaut.casecodec3(Location.apply, Location.unapply)("country", "state", "city")
  implicit val codecMeetUpLocationTrending: CodecJson[MeetUpLocationTrending] = Argonaut.casecodec4(MeetUpLocationTrending.apply, MeetUpLocationTrending.unapply)("country", "groupTopic", "count", "endTs")
  implicit val codecOutputResponse: CodecJson[OutputResponse] = Argonaut.casecodec7(OutputResponse.apply, OutputResponse.unapply)("venue_name", "lon", "lat", "rsvp_id", "group_city", "group_country", "group_state")
  implicit val codecMeetUpLocation: CodecJson[MeetUpLocation] = Argonaut.casecodec3(MeetUpLocation.apply, MeetUpLocation.unapply)("location", "count", "meetUps")
  implicit val codecMeetUpLocationWithDate: CodecJson[MeetUpLocationWithDate] = Argonaut.casecodec4(MeetUpLocationWithDate.apply, MeetUpLocationWithDate.unapply)("location", "count", "meetUps", "endTs")
  implicit val codecMeetUpLocationWindow: CodecJson[MeetUpLocationWindow] = Argonaut.casecodec2(MeetUpLocationWindow.apply, MeetUpLocationWindow.unapply)("endTs", "locations")
}