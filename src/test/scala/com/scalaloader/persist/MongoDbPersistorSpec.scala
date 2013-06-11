package com.scalaloader.persist

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import reactivemongo.bson.{BSONDocument, BSONLong, BSONString}
import com.scalaloader.actor.{Measure, TestCaseCompleteEvent, TestCaseResultEvent, TestPlanResultEvent}
import System._
import scala.util.Random._
import com.scalaloader.persist.mongodb.MongoDbPersistor

/**
 * User: stas
 * Date: 3/21/13, 4:48 PM
 */
class MongoDbPersistorSpec extends WordSpec with ShouldMatchers {

  import MongoDbPersistor._

  "A MongoDbPersistor" should {
    "map  the TestPlanResultEvent to Bson" in {
      val tp = TestPlanResultEvent(s"tp${nextLong()}", s"uuid${nextLong()}", nanoTime(), s"Ok${nextLong()}")
      val tpBson = tpWriter.toBSON(tp).mapped

      tpBson("name").asInstanceOf[BSONString].value should equal(tp.name)
      tpBson("uuid").asInstanceOf[BSONString].value should equal(tp.uuid)
      tpBson("date").asInstanceOf[BSONLong].value should equal(tp.date)
      tpBson("result").asInstanceOf[BSONString].value should equal(tp.result)
    }
    "map  the TestCaseResultEvent to Bson" in {
      validateTestCaseMapping(None)
      validateTestCaseMapping(Some("Error"))
    }
    "map the StatisticsEvent to Bson" in {

    }

    "call the reactcive mongo api in case of persistance" in {

    }
  }

  def validateTestCaseMapping(error: Option[String]) {
    val tc = TestCaseResultEvent(s"tp${nextLong()}",
      TestCaseCompleteEvent(Measure(nanoTime() - nextLong(), nanoTime(), error)))

    val tcBson = tcWriter.toBSON(tc).mapped

    tcBson("parentUuid").asInstanceOf[BSONString].value should equal(tc.parentUuid)

    val measure = tcBson("result").asInstanceOf[BSONDocument].mapped

    measure("start").asInstanceOf[BSONLong].value should equal(tc.result.measure.start)
    measure("end").asInstanceOf[BSONLong].value should equal(tc.result.measure.end)
  }
}
