package com.scalaloader.io.http

import spray.client.pipelining
import akka.actor.ActorRef
import akka.util.Timeout
import concurrent.{Future, ExecutionContext}
import spray.http.{HttpHeaders, HttpCookie}
import concurrent.Promise._
import spray.http.HttpMethods._
import spray.http.HttpRequest
import spray.http.StatusCodes.Redirection
import spray.http.HttpResponse

/**
 * User: stas
 * Date: 25/06/13, 18:58
 */
object FeaturedPipelining {

  import pipelining._

  val maxRedirects = 30

  def sendReceiveWithRedirect(transport: ActorRef, depth: Int = 0)
                             (implicit ec: ExecutionContext, futureTimeout: Timeout): HttpRequest => Future[HttpResponse] =
    request =>
      sendReceive(transport)(ec, futureTimeout).apply(request).flatMap(
        response => {
          (response.status, response.headers.find(_.lowercaseName == "location")) match {
            case (_: Redirection, Some(location)) if depth < maxRedirects =>
              val redirect: HttpRequest => Future[HttpResponse] = sendReceiveWithRedirect(transport, depth + 1)(ec, futureTimeout)
              redirect(request.copy(uri = location.value, method = GET))
            case _ =>
              successful(response).future
          }
        })

  def addCookie(implicit cs: CookieStore): RequestTransformer = cs.get map addCookies getOrElse identity[HttpRequest]

  def readCookie(implicit cs: CookieStore): HttpResponse => HttpResponse = {
    response =>
      cs.put(readCookiesList(response))
      response
  }

  private[http] def addCookies(c: List[HttpCookie]): RequestTransformer = addHeader(HttpHeaders.`Cookie`(c))

  private[http] def readCookiesList(response: HttpResponse) =
    response.headers.collect {
      case HttpHeaders.`Set-Cookie`(c) => c
    } toList
}