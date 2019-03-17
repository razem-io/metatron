name := "metatron"
organization := "io.razem"

version := "0.0.1"

scalaVersion := "2.12.8"

lazy val commonSettings = Seq(
  version := version.value,
  scalaVersion := scalaVersion.value,
  organization := organization.value
)

lazy val metatron_fetcher_homematic = (project in file("modules/fetchers/homematic"))
  .settings(name := "metatron_fetcher_homematic")
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging, UniversalPlugin)
  .settings(
    libraryDependencies += "io.monix" %% "monix-execution" % "3.0.0-RC2",

    libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.13.1",

    libraryDependencies += "com.paulgoldbaum" %% "scala-influxdb-client" % "0.6.1",

    libraryDependencies += "com.typesafe" % "config" % "1.3.2",

    libraryDependencies += "com.beachape" %% "enumeratum" % "1.5.13",

    libraryDependencies += "org.wvlet.airframe" %% "airframe-log" % "19.3.4",

    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.1.1",

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.10.0")
  )

