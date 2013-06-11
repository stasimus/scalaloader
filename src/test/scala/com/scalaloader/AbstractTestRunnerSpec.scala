package com.scalaloader

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito._

/**
 * User: stas
 * Date: 6/11/13, 6:26 PM
 */
class AbstractTestRunnerSpec extends WordSpec with ShouldMatchers {
  "AbstractTestRunner" should {
    "delay execution" in {

      val runner = new AbstractTestRunner with ApplicationContext {
        val method = mock(classOf[() => Unit])
        method()
      }

      runner.method should be (null)

      runner.main(Array[String]())

      verify(runner.method, times(1)).apply()
    }
  }
}
