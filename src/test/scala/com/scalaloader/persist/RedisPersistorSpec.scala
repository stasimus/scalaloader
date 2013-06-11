package com.scalaloader.persist

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import spray.json._
import com.scalaloader.actor.{TestPlanResultEvent, Measure, TestCaseCompleteEvent, TestCaseResultEvent}
import com.scalaloader.persist.redis.JsonProtocol._

/**
 * User: stas
 * Date: 6/11/13, 5:25 PM
 */
class RedisPersistorSpec extends WordSpec with ShouldMatchers {
  "RedisPersistor" should {
    "map TestCaseResultEvent to json" in {
      TestCaseResultEvent("ss", TestCaseCompleteEvent(Measure(120l, 130l))).toJson.toString should be ("""{"parentUuid":"ss","result":{"measure":{"start":120,"end":130}}}""")
    }
    "map TestPlanResultEvent to json" in {
      TestPlanResultEvent("name1", "uuid3", 230l, "result22").toJson.toString should be ("""{"name":"name1","uuid":"uuid3","date":230,"result":"result22"}""")
    }
  }
}
