package com.scalaloader.reporter

import com.scalaloader.actor.{TestCaseResultEvent, TestPlanResultEvent, StatisticsEvent}

/**
 * User: stas
 * Date: 12/06/13, 15:13
 */
object NullResultReporter extends ResultReporter{
  def processTestCase(result: TestCaseResultEvent) {}

  def processTestPlan(result: TestPlanResultEvent) {}

  def report(stat: StatisticsEvent) {}
}
