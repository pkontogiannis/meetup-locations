name := "storage"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.17"
lazy val kafkaClientVersion = "2.2.0"
lazy val argonautVersion = "6.2.3"
lazy val slickVersion = "3.2.3"

lazy val catsVersion = "2.0.0-M1"
lazy val postgresJdbcVersion = "9.4-1206-jdbc42"

libraryDependencies ++= {

  Seq(

    "io.argonaut" %% "argonaut" % argonautVersion,
    "io.argonaut" %% "argonaut-cats" % argonautVersion,

    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "org.apache.kafka" % "kafka-clients" % kafkaClientVersion,

    "org.postgresql" % "postgresql" % postgresJdbcVersion,

    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,

    "org.typelevel" %% "cats-core" % catsVersion,
    "org.typelevel" %% "cats-effect" % catsVersion

  )
}

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

mainClass in (Compile, run)  := Some("com.mtp.Main")
