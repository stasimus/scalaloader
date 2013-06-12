package com.scalaloader.actor

import akka.actor._
import java.util.UUID
import scala.concurrent.duration._

/**
 * User: stas
 * Date: 3/19/13, 5:32 PM
 */


class TestPlanExecutor(resultListener: ActorRef, worker: ActorRef, timeOutSeconds: Int = 60)
  extends Actor with LoggingFSM[TestPlanState, StateData] {

  when(Free) {
    case Event(RunTestPlanEvent(name, list), _) => {
      log.debug("Starting TestPlan...")
      list foreach (tc => worker ! RunTestCaseEvent(tc))
      goto(Processing) using DefinedStateData(sender, name, UUID.randomUUID().toString, list.size)
    }
  }

  when(Processing, stateTimeout = timeOutSeconds seconds) {
    case Event(event: TestCaseCompleteEvent, sd: DefinedStateData) =>
      log.debug("TestCase done event...")

      resultListener ! TestCaseResultEvent(sd.uuid, event)

      if (sd.left > 1) {
        stay using sd.copy(left = sd.left - 1)
      } else {
        moveToDoneState(sd, "succesfull")
      }

    case Event(StateTimeout, sd: DefinedStateData) => {
      moveToDoneState(sd, "timeout")
    }
  }

  def moveToDoneState(sd: DefinedStateData, result: String) = {
    val event = TestPlanResultEvent(sd.name, sd.uuid, System.currentTimeMillis(), result)

    resultListener ! event
    sd.sender ! event

    goto(Done) using InitialStateData
  }

  when(Done) {
    case Event(event, sd) =>
      log.error(s"Unexpected event in Done state: $event = $sd")
      stay
  }

  onTransition {
    case Processing -> Done =>
      context.stop(worker)
      stop(FSM.Shutdown)
  }

  onTermination {
    case StopEvent(_, state, data) =>
      val lastEvents = getLog.mkString("\n\t")
      log.debug(s"Finished with state $state with data $data\nEvents leading up to this point:\n\t$lastEvents")
  }

  startWith(Free, InitialStateData)
}

object TestPlanExecutor {
  def apply(resultListener: ActorRef, worker: ActorRef) = Props(new TestPlanExecutor(resultListener, worker))
}
