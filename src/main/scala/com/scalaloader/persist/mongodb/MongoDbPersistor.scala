package com.scalaloader.persist.mongodb

import reactivemongo.api._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.config.Config
import handlers.BSONWriter
import com.scalaloader.actor.{Measure, StatisticsEvent, TestPlanResultEvent, TestCaseResultEvent}
import com.scalaloader.persist.ResultPersistor

/**
 * User: stas
 * Date: 3/20/13, 12:52 PM
 */
class MongoDbPersistor(val dbName: String, val host: String = "localhost", val port: Int = 27017) extends ResultPersistor {
  this: MongoContext =>
  import MongoDbPersistor._

  val tpResults = db collection("testplan")
  val tcResults = db collection("testcase")
  val otherResults = db collection("other")

  def processTestCase(result: TestCaseResultEvent) {
    tcResults insert(result)
  }

  def processTestPlan(result: TestPlanResultEvent) {
    tpResults insert(result)
  }

  def report(stat: StatisticsEvent) {
    otherResults insert(stat)
  }

  def close:Unit = connection.close()
}

trait MongoContext {
  val dbName: String
  val host: String
  val port: Int

  lazy val connection = MongoConnection(List(s"${host}:${port}"))
  lazy val db = connection(dbName)
}

object MongoDbPersistor {

  implicit def measure2Bson(m: Measure): BSONDocument =
    BSONDocument("start" -> BSONLong(m.start), "end" -> BSONLong(m.end),
      "error" -> m.error.map(BSONString(_)).getOrElse(BSONNull))

  implicit val tpWriter: BSONWriter[TestPlanResultEvent] = new BSONWriter[TestPlanResultEvent] {
    def toBSON(tp: TestPlanResultEvent): BSONDocument =
      BSONDocument("uuid" -> BSONString(tp.uuid), "name" -> BSONString(tp.name),
        "date" -> BSONLong(tp.date), "result" -> BSONString(tp.result))
  }

  implicit val tcWriter: BSONWriter[TestCaseResultEvent] = new BSONWriter[TestCaseResultEvent] {
    def toBSON(tc: TestCaseResultEvent): BSONDocument =
      BSONDocument("parentUuid" -> BSONString(tc.parentUuid), "result" -> measure2Bson(tc.result.measure))
  }

  implicit val statWriter: BSONWriter[StatisticsEvent] = new BSONWriter[StatisticsEvent] {
    def toBSON(document: StatisticsEvent): BSONDocument = ???
  }

  def fromSettings(config: Config) = {
    val prefix = "mongo-db"
    val host = config.getString(f"$prefix.host")
    val port = config.getInt(f"$prefix.port")
    val dbName = config.getString(f"$prefix.db-name")

    new MongoDbPersistor(dbName, host, port) with MongoContext
  }
}
