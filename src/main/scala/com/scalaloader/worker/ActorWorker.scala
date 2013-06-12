package com.scalaloader.worker

import akka.actor.{ActorRef, Props, ActorSystem}
import concurrent.{Future, Await, ExecutionContext}
import akka.util.Timeout
import java.util.UUID._
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import akka.pattern.ask
import com.scalaloader.actor.{TestPlanExecutor, TestCaseExecutor, RunTestPlanEvent}
import com.scalaloader.domain.TestCase
import akka.pattern.gracefulStop


/**
 * User: stas
 * Date: 3/21/13, 12:09 AM
 */
case class ActorWorker(currentMediator: ActorRef)(implicit system: ActorSystem) extends Worker {

  implicit val context: ExecutionContext = system.dispatcher

  val waitDuration: FiniteDuration = 1 hour
  val stopTimeout: Int = 20
  //TODO set the time
  //TODO: make configurable
  implicit val timeout = Timeout(waitDuration)


  def runAndWait(name: String, list: Seq[TestCase], concurrentSize: Int) {
    Await.result(run(concurrentSize, name, list), waitDuration)
  }

  def runAndWait(list: Seq[Future[Any]]) {
    Await.result(Future.sequence(list), waitDuration)
  }

  def run(concurrentSize: Int, name: String, list: Seq[TestCase]) =
    createTestPlanExecutor(concurrentSize) ? RunTestPlanEvent(name, list)

  private def createTestPlanExecutor(concurrentSize: Int) = {
    val uuid = randomUUID().toString

    val workerRouter = system.actorOf(
      Props(new TestCaseExecutor())
        .withRouter(RoundRobinRouter(nrOfInstances = concurrentSize))
        .withDispatcher("thread-per-actor"),
      name = f"workerRouter_$uuid")

    system.actorOf(TestPlanExecutor(currentMediator, workerRouter), name = s"testPlan_$uuid")
  }

  def stop() {
    Await.result(gracefulStop(currentMediator, stopTimeout - 1 seconds), stopTimeout seconds)
    system.shutdown()
  }

  def report(parentName: String, name: String, start: Long, end: Long, error: Option[Throwable] = None) {
    //    currentReporter ! StatisticsEvent(parentName, name, Measure(start, end, error)) //TODO: implement
  }
}
