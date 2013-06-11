package com.scalaloader

import com.scalaloader.dsl.DSL

/**
 * User: stas
 * Date: 6/11/13, 1:16 PM
 */
class AbstractTestRunner extends DSL with DelayedInit {
  this: Context =>

  def delayedInit(body: => Unit) {
    testbody.value = () => body
  }

  def executeTests() {
    testbody.value.apply()
    tearDown()
  }

  def main(args: Array[String]) {
    executeTests()
  }
}