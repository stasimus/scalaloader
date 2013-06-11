package com.scalaloader.dsl


import scala.util.DynamicVariable
import java.lang.System._
import scala.Some
import scala.concurrent.Future

import com.scalaloader.domain.FunctionTestCase
import com.scalaloader.Context

/**
 * User: stas
 * Date: 6/11/13, 1:02 PM
 */
trait DSL {
  this: Context =>
  val testbody = new DynamicVariable[() => Any](() => ???)

  var contextName: Option[String] = None

  object measure {
    def functionality(testPlanName: String) = BlockScope(testPlanName)

    def testPlan(testPlanName: String) = FunctionScope(testPlanName)
  }

  object report {
    def block(name: String) = BlockScope(name)
  }

  case class BlockScope(name: String) {
    def in(block: => Unit) {
      val start = nanoTime()
      val result = try {block; None} catch {case (e: Throwable) => Some(e) }
      val end = nanoTime()

      testExecutor report(contextName.getOrElse(name), name, start, end, result)

      result.foreach(throw _)
    }
  }

  case class FunctionScope(name: String) {
    contextName = Some(name)

    def in(block: => ConcurrentTestPlanExecutor) { block.executeSync() }
  }

  def doing(executor: ConcurrentTestPlanExecutor) = executor

  case class ConcurrentTestPlanExecutor(list: List[Future[Any]]) {
    def concurrentlyWith(another: ConcurrentTestPlanExecutor): ConcurrentTestPlanExecutor =
      ConcurrentTestPlanExecutor(another.list ::: list)

    def executeSync() { testExecutor.runAndWait(list) }
  }

  case class ExecutorBuilder[B](name: String, gen: Seq[B]) {
    def inParallel(f: B => Unit) = asParallelBatch(f)(gen.size)

    def asParallelBatch(f: B => Unit)(batchSize: Int) = ConcurrentTestPlanExecutor(execute(f, batchSize))

    def asSequence(f: B => Unit) = ConcurrentTestPlanExecutor(execute(f, 1))

    def execute(f: (B) => Unit, numberOfWorkers: Int) = {
      List(testExecutor run(numberOfWorkers, name, gen.map(p => FunctionTestCase(p, f))))
    }
  }

  def using[T](gen: Seq[T]) = ExecutorBuilder(contextName.get, gen)

  def tearDown() {
    testExecutor.stop()
  }
}
