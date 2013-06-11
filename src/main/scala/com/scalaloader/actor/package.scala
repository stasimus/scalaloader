package com.scalaloader

import com.scalaloader.domain.TestCase

/**
 * User: stas
 * Date: 6/11/13, 3:03 PM
 */
package object actor {

  sealed trait TestPlanEvent

  case class RunTestPlanEvent(name: String, list: Seq[TestCase]) extends TestPlanEvent

  case class RunTestCaseEvent(testCase: TestCase) extends TestPlanEvent

  case class TestCaseCompleteEvent(measure: Measure) extends TestPlanEvent

  case class TestPlanResultEvent(name: String, uuid: String, date: Long, result: String) extends TestPlanEvent

  case class TestCaseResultEvent(parentUuid: String, result: TestCaseCompleteEvent) extends TestPlanEvent

  case class StatisticsEvent(parentName: String, parentUuid: String, name: String, measure: Measure) extends TestPlanEvent

  case class Measure(start: Long, end: Long, error: Option[String] = None)

  sealed trait TestPlanState

  case object Free extends TestPlanState

  case object Processing extends TestPlanState

  case object Done extends TestPlanState

  sealed trait StateData

  object InitialStateData extends StateData

  case class DefinedStateData(name: String, uuid: String, left: Int) extends StateData

}
