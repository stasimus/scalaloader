package com.scalaloader.reporter

import com.scalaloader.actor.{StatisticsEvent, TestPlanResultEvent, TestCaseResultEvent}

/**
 * User: stas
 * Date: 12/06/13, 15:12
 */
//TODO: probably move into actor
trait ResultReporter {
  def processTestCase(result: TestCaseResultEvent)

  def processTestPlan(result: TestPlanResultEvent)

  def report(stat: StatisticsEvent)
}
