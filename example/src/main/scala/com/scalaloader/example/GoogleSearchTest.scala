package com.scalaloader.example

import com.scalaloader.{ApplicationContext, AbstractTestRunner}
import scala.util.Random

/**
 * User: stas
 * Date: 6/11/13, 12:42 PM
 */
object GoogleSearchTest extends AbstractTestRunner with ApplicationContext {
  measure testPlan "Search on Google" in {
    using(1 to 10) inParallel {
      id =>
        val random: Long = Random.nextInt(3000)

      if (random > 2000)
        throw new RuntimeException()
      else
        Thread.sleep(random)
    }
  }
}
