package org.scalaloader.actor

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

/**
 * User: stas
 * Date: 3/19/13, 7:08 PM
 */
abstract class ActorSpec extends TestKit(ActorSystem("tssys")) with ImplicitSender with WordSpec with ShouldMatchers {
  implicit def functionToAnswer[T](f: InvocationOnMock => T): Answer[T] = new Answer[T] {
    def answer(invocation: InvocationOnMock): T = f(invocation)
  }
}
