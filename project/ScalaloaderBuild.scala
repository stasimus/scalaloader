import sbt._
import sbt.Keys._

object ScalaloaderBuild extends Build {

  lazy val projectResolvers = Seq(
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
  )

  lazy val projectDependecies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.1.1",
    "com.typesafe.akka" %% "akka-testkit" % "2.1.1",
    "org.scalatest" % "scalatest_2.10.0" % "1.8",
    "org.mockito" % "mockito-core" % "1.9.5"
  )

  lazy val scalaloader = Project(
    id = "scalaloader",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "ScalaLoader",
      organization := "org.scalaloader",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      resolvers ++= projectResolvers,
      libraryDependencies ++= projectDependecies
    )
  )
}
