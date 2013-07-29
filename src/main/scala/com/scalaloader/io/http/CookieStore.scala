package com.scalaloader.io.http

import spray.http.HttpCookie
import scala.concurrent.stm._

/**
 * User: stas
 * Date: 25/06/13, 20:37
 */
trait CookieStore {
  def put(list: List[HttpCookie])

  def get: Option[List[HttpCookie]]
}

class StmCookieStore extends CookieStore {
  val curent: Ref[List[HttpCookie]] = Ref(Nil)

  def put(list: List[HttpCookie]) = atomic ( implicit txn => curent.set(list))

  def get: Option[List[HttpCookie]] = curent.single() match {
    case Nil => None
    case ls: List[HttpCookie] => Some(ls)
  }
}

object StmCookieStore {
  def apply = new StmCookieStore
}