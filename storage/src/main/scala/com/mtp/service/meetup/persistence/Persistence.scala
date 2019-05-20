package com.mtp.service.meetup.persistence

import com.mtp.service.meetup.DBDomain._

import scala.concurrent.Future

trait Persistence {

  def getLocation(locationId: Int): Future[Either[Error, Location]]

  def createLocation(location: Location): Future[Either[Error, Location]]

  def updateLocationCounter(locationCounter: LocationCounter): Future[Either[Error, LocationCounter]]

  def getGroupTopic(groupTopicId: Long): Future[Either[Error, GroupTopic]]

  def createCountry(country: Country): Future[Either[Error, Country]]

  def createGroupTopic(groupTopic: GroupTopic): Future[Either[Error, GroupTopic]]

  def updateGroupTopicCounter(groupTopicCounter: GroupTopicCounter): Future[Either[Error, GroupTopicCounter]]
}
