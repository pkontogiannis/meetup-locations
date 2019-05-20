name := "collector"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.5.22"
lazy val akkaHttpVersion = "10.1.8"
lazy val configVersion = "1.3.4"
lazy val kafkaClientVersion = "2.2.0"
lazy val argonautVersion = "6.2.3"

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

libraryDependencies ++= {

  Seq(

    "com.typesafe" % "config" % configVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "org.apache.kafka" % "kafka-clients" % kafkaClientVersion,
    "io.argonaut" %% "argonaut" % argonautVersion,
    "io.argonaut" %% "argonaut-cats" % argonautVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
  )
}
mainClass in(Compile, run) := Some("com.mtp.WebSocketClientFlow")
mainClass in (Compile, packageBin) := Some("com.mtp.WebSocketClientFlow")
mainClass in Universal := Some("com.mtp.WebSocketClientFlow")

enablePlugins(DockerPlugin)

