package org.scalaloader.actor

import org.scalaloader.domain.TestCase


/**
 * User: stas
 * Date: 3/19/13, 5:33 PM
 * Implements TestPlan lifecycle message
 */
sealed trait TestPlanEvent

case class RunTestPlanEvent(name: String, list: Seq[TestCase]) extends TestPlanEvent

case class RunTestCaseEvent(testCase: TestCase) extends TestPlanEvent

case class TestCaseCompleteEvent(measure: Measure) extends TestPlanEvent

case class TestPlanResultEvent(name: String, uuid: String, date: Long, result: String) extends TestPlanEvent

case class TestCaseResultEvent(uuid: String, result: TestCaseCompleteEvent) extends TestPlanEvent

case class Statistics(parentName: String, name: String, measure: Measure) extends TestPlanEvent

case class Measure(start: Long, end: Long, error: Option[Throwable] = None)