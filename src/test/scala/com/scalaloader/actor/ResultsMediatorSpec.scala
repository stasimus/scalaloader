package com.scalaloader.actor

import org.mockito.Mockito._
import akka.testkit.TestActorRef
import com.scalaloader.persist.ResultPersistor
import java.util.UUID._
import System._
import scala.util.Random._
import org.mockito.Matchers._
import com.scalaloader.reporter.ResultReporter

/**
 * User: stas
 * Date: 3/20/13, 12:20 PM
 */
class ResultsMediatorSpec extends ActorSpec {
  "A ResultsMediatorSpec" should {
    "react on all supported messages via delegating to persistence" in {
      val resultProcessor = mock(classOf[ResultPersistor])
      val resultReporter = mock(classOf[ResultReporter])

      val actorRef = TestActorRef(new ResultsMediator(resultProcessor, resultReporter))
      val uuid = randomUUID().toString

      val event1 = TestPlanResultEvent("testing ...", uuid, nanoTime(), "result is ...")

      actorRef ! event1

      verify(resultProcessor).processTestPlan(event1)

      actorRef ! TestCaseResultEvent("", TestCaseCompleteEvent(Measure(nanoTime - nextInt, nanoTime)))
      actorRef ! TestCaseResultEvent("", TestCaseCompleteEvent(buildErrorMeasure))

      verify(resultProcessor, times(2)).processTestCase(any[TestCaseResultEvent])

      actorRef ! StatisticsEvent("parent", uuid, "st", buildErrorMeasure)
    }
  }

  def buildErrorMeasure = Measure(nanoTime - nextInt, nanoTime, Some("Exception"))
}
