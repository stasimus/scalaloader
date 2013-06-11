package com.scalaloader.persist

import com.scalaloader.actor.{TestCaseResultEvent, TestPlanResultEvent, StatisticsEvent}

/**
 * User: stas
 * Date: 6/11/13, 7:12 PM
 */
class NullPersistor extends ResultPersistor{
  def processTestCase(result: TestCaseResultEvent) {}

  def processTestPlan(result: TestPlanResultEvent) {}

  def report(stat: StatisticsEvent) {}

  def close() {}
}
