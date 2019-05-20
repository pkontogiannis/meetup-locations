name := "processor"

version := "0.1"

scalaVersion := "2.12.8"


lazy val akkaVersion = "2.5.17"
lazy val kafkaClientVersion = "2.2.0"
lazy val argonautVersion = "6.2.3"
lazy val slickVersion = "3.2.3"
lazy val configVersion = "1.3.4"

lazy val catsVersion = "2.0.0-M1"
lazy val postgresJdbcVersion = "9.4-1206-jdbc42"
lazy val flinkVersion = "1.8.0"

libraryDependencies ++= {

  Seq(

    "com.typesafe" % "config" % configVersion,
    "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,

    "io.argonaut" %% "argonaut" % argonautVersion,
    "io.argonaut" %% "argonaut-cats" % argonautVersion,

    "org.apache.flink" %% "flink-scala" % flinkVersion,
    "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
    "org.apache.flink" %% "flink-streaming-scala" % flinkVersion

  )
}

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

mainClass in (Compile, run)  := Some("com.flink.GroupByLocation")
