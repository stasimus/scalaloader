package com.scalaloader.actor

import akka.actor.{Props, ActorLogging, Actor}
import com.scalaloader.persist.ResultPersistor
import com.scalaloader.reporter.ResultReporter

/**
 * User: stas
 * Date: 3/20/13, 1:35 AM
 */
class ResultsMediator(persistor: ResultPersistor, reporter: ResultReporter) extends Actor with ActorLogging {
  def receive = {
    case testPlan: TestPlanResultEvent =>
      log.debug(s"${testPlan.name} = ${testPlan.result}")
      reporter processTestPlan testPlan
      persistor processTestPlan testPlan
    case stat: StatisticsEvent =>
      log.debug(s"${stat.parentName}, ${stat.name}, ${stat.measure.start}, ${stat.measure.end}, ${stat.measure.error}")
      reporter report stat
      persistor report stat
    case testCase: TestCaseResultEvent =>
      log.debug(s"${testCase.parentUuid} = ${testCase.result}")
      reporter processTestCase testCase
      persistor processTestCase testCase
  }
}

object ResultsMediator {
  def apply(persistor: ResultPersistor, reporter: ResultReporter) = Props(new ResultsMediator(persistor, reporter))
}
