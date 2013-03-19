package org.scalaloader.actor

import org.mockito.Mockito._
import akka.testkit.TestActorRef
import org.scalaloader.domain.TestCase
import org.scalaloader.TestCaseExecutor
import scala.concurrent.duration._
import org.mockito.invocation.InvocationOnMock

/**
 * User: stas
 * Date: 3/19/13, 11:05 PM
 */
class TestCaseExecutorSpec extends ActorSpec {


  "A TestCaseExecutor" should {
    "handle success and error cases" in {
      val testCase = mock(classOf[TestCase])
      val actorRef = TestActorRef(new TestCaseExecutor())
      actorRef ! RunTestCaseEvent(testCase)

      val message = receiveOne(100 milliseconds).asInstanceOf[TestCaseCompleteEvent]

      (message.error) should equal(None)

      val error = new RuntimeException
      when(testCase.test()).thenThrow(error)

      actorRef ! RunTestCaseEvent(testCase)

      val message2 = receiveOne(100 milliseconds).asInstanceOf[TestCaseCompleteEvent]
      (message2.error) should equal(Some(error))
    }
    "aproximetly mesure execution time" in {
      val testCase = mock(classOf[TestCase])
      val actorRef = TestActorRef(new TestCaseExecutor())
      when(testCase.test()) thenAnswer {
        inv: InvocationOnMock => Thread.sleep(1200)
      }

      actorRef ! RunTestCaseEvent(testCase)

      val message = receiveOne(1500 milliseconds).asInstanceOf[TestCaseCompleteEvent]
      assert(message.end - message.start >= 1200)
    }
  }
}
