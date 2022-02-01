ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.1"

lazy val root = (project in file("."))
  .settings(
    name := "wc"
  )

lazy val akkaVersion = "2.6.18"

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "3.2.4",
  "co.fs2" %% "fs2-io" % "3.2.4",
  "org.typelevel" %% "cats-effect" % "3.3.4"
)