name := "Metatron"
organization := "io.razem"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += Resolver.bintrayRepo("hmil", "maven")

libraryDependencies += "fr.hmil" %% "roshttp" % "2.2.3"

libraryDependencies += "com.paulgoldbaum" %% "scala-influxdb-client" % "0.6.1"

libraryDependencies += "com.typesafe" % "config" % "1.3.2"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)