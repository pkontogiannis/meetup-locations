package com.mtp.service.meetup.persistence

import com.mtp.service.meetup.DBDomain._
import com.mtp.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class PersistenceSQL(val dbAccess: DBAccess) extends Persistence {

  import dbAccess._
  import dbAccess.profile.api._

  override def getLocation(locationId: Int): Future[Either[Error, Location]] = {
    db.run(Locations.filter(_.id.get === locationId).result.headOption).
      transformWith {
        case Success(optLocation) => optLocation match {
          case Some(location) => Future.successful(Right(location))
          case None => Future.successful(Left(new Error))
        }
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

  override def createLocation(location: Location): Future[Either[Error, Location]] = {
    val insertQuery = Locations returning Locations.map(_.id) into ((item, id) => item.copy(id = id))

    db.run(Locations.filter(tuple =>
      tuple.country.trim.toLowerCase === location.country.trim.toLowerCase
//        && tuple.state === location.state
        && tuple.city.trim.toLowerCase === location.city.trim.toLowerCase
    ).result.headOption)
      .flatMap {
        case Some(loc) => Future.successful(loc)
        case None =>
          val action = insertQuery += Location(Some(0), location.country.trim.toLowerCase, location.state, location.city.trim.toLowerCase)
          db.run(action)
      }
      .transformWith {
        case Success(loc) => Future.successful(Right(loc))
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

  def updateLocationCounter(locationCounter: LocationCounter): Future[Either[Error, LocationCounter]] = {
    db.run(LocationsCounters.filter(tuple => tuple.locationId === locationCounter.locationId).result.headOption)
      .map {
        case Some(lc) =>
          val q = for {c <- LocationsCounters if c.locationId === lc.locationId} yield (c.counter, c.time)
          val updateAction = q.update(locationCounter.counter, locationCounter.time)
          db.run(updateAction)
        case None =>
          db.run(LocationsCounters += locationCounter)
      }
      .transformWith {
        case Success(_) => Future.successful(Right(locationCounter))
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

  def getGroupTopic(groupTopicId: Long): Future[Either[Error, GroupTopic]] = {
    db.run(GroupTopics.filter(_.id.get === groupTopicId).result.headOption).
      transformWith {
        case Success(optGroupTopic) => optGroupTopic match {
          case Some(groupTopic) => Future.successful(Right(groupTopic))
          case None => Future.successful(Left(new Error))
        }
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

  def createCountry(country: Country): Future[Either[Error, Country]] = {
    val insertQuery = Countries returning Countries.map(_.id) into ((item, id) => item.copy(id = id))

    db.run(Countries.filter(tuple =>
      tuple.name.trim.toLowerCase === country.name.trim.toLowerCase
    ).result.headOption)
      .flatMap {
        case Some(cntr) => Future.successful(cntr)
        case None =>
          val action = insertQuery += Country(Some(0), country.name.trim.toLowerCase)
          db.run(action)
      }
      .transformWith {
        case Success(cntr) => Future.successful(Right(cntr))
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

  def createGroupTopic(groupTopic: GroupTopic): Future[Either[Error, GroupTopic]] = {
    val insertQuery = GroupTopics returning GroupTopics.map(_.id) into ((item, id) => item.copy(id = id))

    db.run(GroupTopics.filter(tuple =>
      tuple.urlKey.trim.toLowerCase === groupTopic.urlKey.trim.toLowerCase
        && tuple.topicName.trim.toLowerCase === groupTopic.topicName.trim.toLowerCase
    ).result.headOption)
      .flatMap {
        case Some(gt) => Future.successful(gt)
        case None =>
          val action = insertQuery += GroupTopic(Some(0), groupTopic.urlKey.trim.toLowerCase, groupTopic.topicName.trim.toLowerCase)
          db.run(action)
      }
      .transformWith {
        case Success(gt) => Future.successful(Right(gt))
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

  def updateGroupTopicCounter(groupTopicCounter: GroupTopicCounter): Future[Either[Error, GroupTopicCounter]] = {
    db.run(GroupsTopicsCounters.filter(tuple =>
      tuple.countryId === groupTopicCounter.countryId
        && tuple.groupTopicId === groupTopicCounter.groupTopicId
    ).result.headOption)
      .map {
        case Some(lc) =>
          val q = for {c <- GroupsTopicsCounters if c.countryId === lc.countryId && c.groupTopicId === lc.groupTopicId} yield (c.counter, c.time)
          val updateAction = q.update(groupTopicCounter.counter, groupTopicCounter.time)
          db.run(updateAction)
        case None =>
          db.run(GroupsTopicsCounters += groupTopicCounter)
      }
      .transformWith {
        case Success(_) => Future.successful(Right(groupTopicCounter))
        case Failure(_) => Future.successful(Left(new Error))
      }
  }

}
