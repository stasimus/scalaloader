package com.scalaloader.reporter.console

import com.scalaloader.reporter.ResultReporter
import com.scalaloader.actor.{TestCaseResultEvent, TestPlanResultEvent, StatisticsEvent}
import akka.event.{Logging, LoggingAdapter}
import akka.actor.ActorSystem
import PrintableImplicits._

/**
 * User: stas
 * Date: 12/06/13, 15:37
 */
//TODO implement error handling
class ConsoleLogginReporter(log: LoggingAdapter) extends ResultReporter {

  def processTestCase(result: TestCaseResultEvent) {
    log info toConsoleText(result)
  }

  def processTestPlan(result: TestPlanResultEvent) {
    log info result.toString
  }

  def toConsoleText[A <% Printable](print: A) = print.toConsoleString

  def report(stat: StatisticsEvent) = ???
}

object ConsoleLogginReporter {
  def apply()(implicit system: ActorSystem) = new ConsoleLogginReporter(Logging(system, "reporter"))
}