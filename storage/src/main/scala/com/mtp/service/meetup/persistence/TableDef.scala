package com.mtp.service.meetup.persistence

import com.mtp.persistence.SlickJdbcProfile
import com.mtp.service.meetup.DBDomain._
import slick.lifted.ProvenShape

trait TableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  class LocationTable(tag: Tag) extends Table[Location](tag, "location") {

    def id: Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def country: Rep[String] = column[String]("country")

    def state: Rep[Option[String]] = column[Option[String]]("state")

    def city: Rep[String] = column[String]("city")

    def idx = index("idx_a", (country, state, city), unique = true)

    def * : ProvenShape[Location] = (id, country, state, city) <> (Location.tupled, Location.unapply)

  }

  class LocationCounterTable(tag: Tag) extends Table[LocationCounter](tag, "locationcounter") {

    def locationId: Rep[Int] = column[Int]("locationId", O.Unique)

    def counter: Rep[Int] = column[Int]("counter")

    def time: Rep[Long] = column[Long]("time")

    def * : ProvenShape[LocationCounter] = (locationId, counter, time) <> (LocationCounter.tupled, LocationCounter.unapply)

  }

  class CountryTable(tag: Tag) extends Table[Country](tag, "country") {

    def id: Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def * : ProvenShape[Country] = (id, name) <> (Country.tupled, Country.unapply)

  }

  class GroupTopicTable(tag: Tag) extends Table[GroupTopic](tag, "grouptopic") {

    def id: Rep[Option[Long]] = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def urlKey: Rep[String] = column[String]("urlKey")

    def topicName: Rep[String] = column[String]("topicName")

    def * : ProvenShape[GroupTopic] = (id, urlKey, topicName) <> (GroupTopic.tupled, GroupTopic.unapply)

  }


  class GroupTopicCounterTable(tag: Tag) extends Table[GroupTopicCounter](tag, "grouptopiccounter") {

    def countryId: Rep[Int] = column[Int]("countryId")

    def groupTopicId: Rep[Long] = column[Long]("groupTopicId")

    def counter: Rep[Int] = column[Int]("counter")

    def time: Rep[Long] = column[Long]("time")

    def pk = primaryKey("pk_a", (countryId, groupTopicId))

    def * : ProvenShape[GroupTopicCounter] = (countryId, groupTopicId, counter, time) <> (GroupTopicCounter.tupled, GroupTopicCounter.unapply)

  }


}
