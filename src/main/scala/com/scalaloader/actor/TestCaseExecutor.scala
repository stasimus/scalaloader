package com.scalaloader.actor

import akka.actor.{ActorLogging, Actor}
import System._

/**
 * User: stas
 * Date: 3/19/13, 7:14 PM
 */
class TestCaseExecutor extends Actor with ActorLogging {
  def receive = {
    case RunTestCaseEvent(testCase) =>
      val start = nanoTime
      try {
        testCase.test()
        sender ! TestCaseCompleteEvent(Measure(start, nanoTime))
      }
      catch {
        case error: Throwable =>
          sender ! TestCaseCompleteEvent(Measure(start, nanoTime, Some(error.toString)))
          throw error
      } finally {
        log.debug(s"TestCase ${testCase} executed")
      }
  }
}
