package com.scalaloader.persist

import com.scalaloader.actor.{StatisticsEvent, TestPlanResultEvent, TestCaseResultEvent}


/**
 * User: stas
 * Date: 3/20/13, 1:41 AM
 */
trait ResultPersistor {
  def processTestCase(result: TestCaseResultEvent)

  def processTestPlan(result: TestPlanResultEvent)

  def report(stat: StatisticsEvent)

  def close()
}
