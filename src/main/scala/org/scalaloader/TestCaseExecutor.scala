package org.scalaloader

import actor.{RunTestCaseEvent, TestCaseCompleteEvent}
import akka.actor.Actor
import System._

/**
 * User: stas
 * Date: 3/19/13, 7:14 PM
 */
class TestCaseExecutor extends Actor {
  def receive = {
    case RunTestCaseEvent(testCase) =>
      val start = nanoTime
      try {
        testCase.test()
        sender ! TestCaseCompleteEvent(start, nanoTime)
      }
      catch {
        case error: Throwable =>
          sender ! TestCaseCompleteEvent(start, nanoTime, Some(error))
          throw error
      }
  }
}
