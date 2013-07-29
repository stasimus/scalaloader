package com.scalaloader.io.http

import spray.http._
import spray.client.pipelining._
import scala.actors.Future
import akka.actor.ActorSystem
import FeaturedPipelining._

/**
 * User: stas
 * Date: 25/06/13, 15:32
 */
class HttpClient(implicit ec: ActorSystem) {

  implicit val cs = StmCookieStore

  val pipeline: HttpRequest => Future[Int] = (
    addCookie
      ~> sendReceive
      ~> unmarshal[Int]
    )

}
