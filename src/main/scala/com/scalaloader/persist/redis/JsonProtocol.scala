package com.scalaloader.persist.redis

import spray.json.DefaultJsonProtocol
import com.scalaloader.actor.{TestPlanResultEvent, TestCaseResultEvent, TestCaseCompleteEvent, Measure}

/**
 * User: stas
 * Date: 6/11/13, 6:00 PM
 */
object JsonProtocol extends DefaultJsonProtocol {
  implicit val measureFormat = jsonFormat3(Measure)
  implicit val testCaseCompleteEventForamt = jsonFormat1(TestCaseCompleteEvent)
  implicit val testCaseResultEveentFormat = jsonFormat2(TestCaseResultEvent)

  implicit val testPlanResultEvent = jsonFormat4(TestPlanResultEvent)
}
