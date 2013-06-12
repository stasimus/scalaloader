package com.scalaloader

import com.typesafe.config.{ConfigFactory, Config}
import com.scalaloader.persist.{ResultPersistor}
import com.scalaloader.worker.{Worker, ActorWorker}
import com.scalaloader.persist.redis.RedisPersistor
import com.scalaloader.reporter.ResultReporter
import com.scalaloader.reporter.console.ConsoleLogginReporter
import akka.actor.{Props, ActorSystem}
import com.scalaloader.actor.ResultsMediator

/**
 * User: stas
 * Date: 6/11/13, 1:05 PM
 */
trait Context {
  val config: Config
  val resultPersistor: ResultPersistor
  val resultReporter: ResultReporter
  val worker: Worker
}

trait ApplicationContext extends Context {
  implicit lazy val config = ConfigFactory load

  implicit lazy val system = ActorSystem("TestPlanSystem")

  val resultPersistor: ResultPersistor = RedisPersistor()
  val resultReporter: ResultReporter = ConsoleLogginReporter()

  val currentMediator = system.actorOf(
    Props(new ResultsMediator(resultPersistor, resultReporter)), name = "resultListener")

  lazy val worker = ActorWorker(currentMediator)
}
