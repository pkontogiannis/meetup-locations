package com.mtp.service.meetup

import com.mtp.service.meetup.DBDomain._
import com.mtp.service.meetup.persistence.Persistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MeetUpServiceDefault(val locationPersistence: Persistence) extends MeetUpService {

  override def getLocation(locationId: Int): Future[Either[Error, Location]] = {
    locationPersistence.getLocation(locationId).map {
      case Right(loc) => Right(loc)
      case Left(_) => Left(new Error)
    }
  }

  override def createLocation(location: Location): Future[Either[Error, Location]] = {
    locationPersistence.createLocation(location).map {
      case Right(value) => Right(value)
      case Left(_) => Left(new Error)
    }
  }

  def updateLocationCounter(locationCounter: LocationCounter): Future[Either[Error, LocationCounter]] = {
    locationPersistence.updateLocationCounter(locationCounter).map {
      case Right(value) => Right(value)
      case Left(_) => Left(new Error)
    }
  }

  def getGroupTopic(groupTopicId: Long): Future[Either[Error, GroupTopic]] = {
    locationPersistence.getGroupTopic(groupTopicId).map {
      case Right(value) => Right(value)
      case Left(_) => Left(new Error)
    }
  }

  def createCountry(country: Country): Future[Either[Error, Country]] = {
    locationPersistence.createCountry(country).map {
      case Right(value) => Right(value)
      case Left(_) => Left(new Error)
    }
  }


  def createGroupTopic(groupTopic: GroupTopic): Future[Either[Error, GroupTopic]] = {
    locationPersistence.createGroupTopic(groupTopic).map {
      case Right(value) => Right(value)
      case Left(_) => Left(new Error)
    }
  }


  def updateGroupTopicCounter(groupTopicCounter: GroupTopicCounter): Future[Either[Error, GroupTopicCounter]] = {
    locationPersistence.updateGroupTopicCounter(groupTopicCounter).map {
      case Right(value) => Right(value)
      case Left(_) => Left(new Error)
    }
  }


}
