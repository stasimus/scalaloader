package com.scalaloader.actor

import akka.testkit.{TestFSMRef, TestProbe}
import com.scalaloader.domain.TestCase
import org.mockito.Mockito._
import scala.concurrent.duration._

/**
 * User: stas
 * Date: 3/19/13, 5:55 PM
 */
class TestPlanExecutorSpec extends ActorSpec {
  "A TestPlanExecutor" should {
    "work with 2 TestCases and ignore unexpected messages" in {
      val (workerProbe, resultProbe) = createTestProbes

      val testPlan = createTestPlanActor(resultProbe, workerProbe)

      testPlan ! RunTestPlanEvent("tp name", createTestCasesList(2))

      within(1 second) {
        workerProbe.expectMsgType[RunTestCaseEvent]
        workerProbe reply (TestCaseCompleteEvent(Measure(0, 1000)))
        resultProbe.expectMsgType[TestCaseResultEvent]

        workerProbe.expectMsgType[RunTestCaseEvent]
        workerProbe reply (TestCaseCompleteEvent(Measure(0, 1200)))
        resultProbe.expectMsgType[TestCaseResultEvent]
        resultProbe.expectMsgType[TestPlanResultEvent]
      }

      testPlan ! TestCaseCompleteEvent(Measure(0, 0))
      workerProbe.expectNoMsg(500 milliseconds)
    }
    "react on timeout" in {
      val (workerProbe, resultProbe) = createTestProbes

      val testPlan = createTestPlanActor(resultProbe, workerProbe, 1)

      testPlan ! RunTestPlanEvent("tp name", createTestCasesList(1))

      within(3 seconds) {
        workerProbe.expectMsgType[RunTestCaseEvent]
        resultProbe.expectNoMsg(800 milliseconds)
        resultProbe.expectMsgType[TestPlanResultEvent](400 millisecond)
        resultProbe.expectNoMsg(1200 milliseconds) //expect no any TestCase result
      }

      within(1 second) {
        workerProbe reply (TestCaseCompleteEvent(Measure(0, 1200)))
        resultProbe.expectNoMsg(500 milliseconds)
      }
    }
    "changing the states according lifecycle" in {
      val (workerProbe, resultProbe) = createTestProbes

      val testPlan = createTestPlanActor(resultProbe, workerProbe)

      assert(testPlan.stateName == Free)

      testPlan ! RunTestPlanEvent("tp name", createTestCasesList(3))

      (3 to 1 by -1) foreach {
        i =>
          (testPlan.stateName) should equal(Processing)
          checkTheStateData(testPlan.stateData, i)

          workerProbe.expectMsgType[RunTestCaseEvent](100 milliseconds)
          workerProbe reply (TestCaseCompleteEvent(Measure(0, 1000)))
      }

      (testPlan.stateName) should equal(Done)
    }
  }


  def checkTheStateData(stateData: StateData, left: Int) {
    assert(stateData.isInstanceOf[DefinedStateData])

    (stateData.asInstanceOf[DefinedStateData].left) should equal(left)
  }

  def createTestProbes = (TestProbe(), TestProbe())

  def createTestPlanActor(resultProbe: TestProbe, workerProbe: TestProbe, timeOut: Int = 60) =
    TestFSMRef(new TestPlanExecutor(resultProbe.ref, workerProbe.ref, timeOut))

  def createTestCasesList(size: Int) = (1 to size) map (i => mock(classOf[TestCase]))
}
