package com.flink.config

object Version extends Version

trait Version {
  def version: String = "v01"
}
