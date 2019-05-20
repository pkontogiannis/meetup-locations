package com.flink.service.location.persistence

import com.flink.service.errors.ServiceError
import com.flink.service.errors.ServiceError.NotAvailableData
import com.flink.service.location.DBDomain.{GroupTopicTrendResult, LocationResult}
import com.flink.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class LocationPersistenceSQL(val dbAccess: DBAccess) extends LocationPersistence {

  import dbAccess._
  import slick.driver.PostgresDriver.api._

  def getLocations(limit: Int): Future[Either[ServiceError, List[LocationResult]]] = {

    db.run(
      (LocationsCounters.sortBy(_.counter.desc).take(limit)
        join Locations on (_.locationId === _.id)).result
    ).
      transformWith {
        case Success(list) =>
          val res = list.map(l => LocationResult(l._2.country, l._2.state, l._2.city, l._1.counter, l._1.time))
          Future.successful(Right(res.toList))
        case Failure(_) => Future.successful(Left(NotAvailableData()))
      }
  }


  def getGroupTopicTrends(country: String, limit: Int): Future[Either[ServiceError, List[GroupTopicTrendResult]]] = {

    /*
    SELECT country.name, grouptopic."topicName", grouptopiccounter.counter, grouptopiccounter.time
      FROM grouptopiccounter
      join country on (grouptopiccounter."countryId" = country."id")
      join grouptopic on (grouptopic."id" = grouptopiccounter."groupTopicId")
      where country."name"='gb'
      order by counter desc;
     */
    db.run(
      (
        Countries.filter(_.name.trim.toLowerCase === country.trim.toLowerCase)
          join GroupsTopicsCounters.sortBy(_.counter.desc) on (_.id === _.countryId)
          join GroupTopics on (_._2.groupTopicId === _.id)
        ).take(limit).result
    ).
      transformWith {
        case Success(list) =>
          val res = list.map(gtt =>
            GroupTopicTrendResult(gtt._1._1.name, gtt._2.topicName, gtt._2.urlKey, gtt._1._2.counter, gtt._1._2.time)
          )
          Future.successful(Right(res.toList))
        case Failure(_) => Future.successful(Left(NotAvailableData()))
      }
  }


}
