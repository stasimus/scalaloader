package com.scalaloader.persist.redis


import com.top10.redis.SingleRedis
import com.scalaloader.actor.TestCaseResultEvent
import com.scalaloader.actor.StatisticsEvent
import com.scalaloader.actor.TestPlanResultEvent
import spray.json._
import com.scalaloader.persist.ResultPersistor
import JsonProtocol._

/**
 * User: stas
 * Date: 6/11/13, 5:14 PM
 */
case class RedisPersistor(host: String = "localhost", port: Int = 6379) extends ResultPersistor {

  lazy val redis = new SingleRedis(host, port)

  def processTestCase(result: TestCaseResultEvent) {
    redis.exec {
      pipe =>
        pipe.lpush("testCaseResults", result.toJson.toString)
    }
  }

  def processTestPlan(result: TestPlanResultEvent) {
    redis.exec {
      pipe =>
        pipe.lpush("testPlanResultEvent", result.toJson.toString)
    }
  }

  def report(stat: StatisticsEvent) {
    //TODO: implement
  }

  def close() {}
}


