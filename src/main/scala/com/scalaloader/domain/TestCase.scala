package com.scalaloader.domain

/**
 * User: stas
 * Date: 3/19/13, 5:35 PM
 */
trait TestCase extends Serializable {
  def test()
}

case class FunctionTestCase[T](p: T, f: T => Unit) extends TestCase {
  def test = f apply p
}
