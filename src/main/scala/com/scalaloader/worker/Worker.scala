package com.scalaloader.worker

import concurrent.Future
import com.scalaloader.domain.TestCase

/**
 * User: stas
 * Date: 3/21/13, 12:05 AM
 */
trait Worker {
  def runAndWait(name: String, list: Seq[TestCase], numberOfInstances: Int)

  def runAndWait(list: Seq[Future[Any]])

  def run(concurrentSize: Int, name: String, list: Seq[TestCase]): Future[Any]

  def stop()

  /**
   * Fire and forget style
   */
  @deprecated
  def report(parentName: String, name: String, start: Long, end: Long, error: Option[Throwable] = None)
}
