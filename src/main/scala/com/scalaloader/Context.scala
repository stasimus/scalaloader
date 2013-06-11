package com.scalaloader

import com.typesafe.config.{ConfigFactory, Config}
import com.scalaloader.persist.{ResultPersistor}
import com.scalaloader.worker.{Worker, ActorWorker}
import com.scalaloader.persist.redis.RedisPersistor

/**
 * User: stas
 * Date: 6/11/13, 1:05 PM
 */
trait Context {
  val config: Config
  val resultPersistor: ResultPersistor
  val testExecutor: Worker
}

trait ApplicationContext extends Context {
  implicit lazy val config = ConfigFactory load
  lazy val resultPersistor: ResultPersistor = RedisPersistor()
  lazy val testExecutor: Worker = ActorWorker(resultPersistor)
}
