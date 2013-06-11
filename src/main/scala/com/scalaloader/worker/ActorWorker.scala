package com.scalaloader.worker

import akka.actor.{Props, ActorSystem}
import concurrent.{Future, Await, ExecutionContext}
import akka.util.Timeout
import java.util.UUID._
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import akka.pattern.ask
import com.scalaloader.persist.ResultPersistor
import com.scalaloader.actor.{TestPlanExecutor, TestCaseExecutor, RunTestPlanEvent, ResultsMediator}
import com.scalaloader.domain.TestCase


/**
 * User: stas
 * Date: 3/21/13, 12:09 AM
 */
case class ActorWorker(resultPersistor: ResultPersistor) extends Worker {
  implicit val system = ActorSystem("TestPlanSystem")
  implicit val context: ExecutionContext = system.dispatcher

  val waitDuration: FiniteDuration = 1 hour
  //TODO: make configurable
  implicit val timeout = Timeout(waitDuration)

  val currentReporter = system.actorOf(
    Props(new ResultsMediator(resultPersistor)), name = "resultListener")

  def runAndWait(name: String, list: Seq[TestCase], concurrentSize: Int) {
    Await.result(run(concurrentSize, name, list), waitDuration)
  }

  def runAndWait(list: Seq[Future[Any]]) {
    Await.result(Future.sequence(list), waitDuration)
  }

  def run(concurrentSize: Int, name: String, list: Seq[TestCase]) =
    createTestPlan(concurrentSize) ? RunTestPlanEvent(name, list)

  private def createTestPlan(concurrentSize: Int) = {
    val uuid = randomUUID().toString

    val workerRouter = system.actorOf(
      Props(new TestCaseExecutor())
        .withRouter(RoundRobinRouter(nrOfInstances = concurrentSize))
        .withDispatcher("thread-per-actor"),
      name = f"workerRouter_$uuid")

    system.actorOf(Props(TestPlanExecutor(currentReporter, workerRouter)), name = f"testPlan_$uuid")
  }

  def stop() {
    system.shutdown()
    resultPersistor.close()
  }

  def report(parentName: String, name: String, start: Long, end: Long, error: Option[Throwable] = None) {
//    currentReporter ! StatisticsEvent(parentName, name, Measure(start, end, error)) //TODO: implement
  }
}
