ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "testTask"
  )


libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3"

libraryDependencies += "co.fs2" %% "fs2-io" % "3.9.4"

libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.8.4" % Test
