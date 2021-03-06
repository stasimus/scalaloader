import sbt._
import sbt.Keys._
import scala.Some

object ScalaloaderBuild extends Build {

  lazy val projectResolvers = Seq(
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
    "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
    "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
    "spray" at "http://repo.spray.io/",
    "spray repo" at "http://repo.spray.io/"
  )

  lazy val akkaVersion = "2.1.4"
  val destVersion = "2.10.0"
  val sprayVersion = "1.2-M8"

  lazy val projectDependecies = Seq(
    "io.spray" %  "spray-client" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "org.scalatest" %% "scalatest" % "1.9.1" withSources(),
    "org.mockito" % "mockito-core" % "1.9.5",
    "org.reactivemongo" %% "reactivemongo" % "0.8" withSources(),
    "com.top10" %% "scala-redis-client" % "1.13.0" withSources(),
    "io.spray" %% "spray-json" % "1.2.5",
    "ch.qos.logback" % "logback-classic" % "1.0.7",
    "org.scala-stm" %% "scala-stm" % "0.7"
  )

  lazy val scalaloader = Project(
    id = "scalaloader",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "ScalaLoader",
      organization := "com.scalaloader",
      exportJars := true,
      version := "0.1-SNAPSHOT",
      scalaVersion := destVersion,
      resolvers ++= projectResolvers,
      libraryDependencies ++= projectDependecies
    )
  )

  lazy val scalaloaderExample = Project(
    id = "example",
    base = file("example"),
    settings = Project.defaultSettings ++ Seq(
      mainClass := Some("com.scalaloader.example.GoogleSearchTest"),
      name := "ScalaLoaderExample",
      organization := "com.scalaloader",
      version := "0.1-SNAPSHOT",
      scalaVersion := destVersion,
      libraryDependencies ++= projectDependecies
    )
  ) dependsOn scalaloader
}
