package com.scalaloader.actor

import akka.actor.{ActorLogging, Actor}
import com.scalaloader.persist.ResultPersistor

/**
 * User: stas
 * Date: 3/20/13, 1:35 AM
 */
class ResultsMediator(persistor: ResultPersistor) extends Actor with ActorLogging {
  def receive = {
    case testPlan: TestPlanResultEvent =>
      log.debug(f"${testPlan.name} = ${testPlan.result}")
      persistor.processTestPlan(testPlan)
    case stat: StatisticsEvent =>
      log.debug(f"${stat.parentName}, ${stat.name}, ${stat.measure.start}, ${stat.measure.end}, ${stat.measure.error}")
      persistor.report(stat)
    case testCase: TestCaseResultEvent =>
      log.debug(f"${testCase.parentUuid} = ${testCase.result}")
      persistor.processTestCase(testCase)
  }
}
