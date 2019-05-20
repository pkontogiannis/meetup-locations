package com.flink.persistence

import com.flink.service.location.persistence.TableDef


trait Schema extends SlickJdbcProfile
  with TableDef {

  import profile.api._

  implicit lazy val Locations: TableQuery[LocationTable] = TableQuery[LocationTable]
  implicit lazy val LocationsCounters: TableQuery[LocationCounterTable] = TableQuery[LocationCounterTable]
  implicit lazy val Countries: TableQuery[CountryTable] = TableQuery[CountryTable]
  implicit lazy val GroupTopics: TableQuery[GroupTopicTable] = TableQuery[GroupTopicTable]
  implicit lazy val GroupsTopicsCounters: TableQuery[GroupTopicCounterTable] = TableQuery[GroupTopicCounterTable]

  lazy val Schema: profile.DDL =
    Locations.schema ++
      LocationsCounters.schema ++
      Countries.schema ++
      GroupTopics.schema ++
      GroupsTopicsCounters.schema

}
