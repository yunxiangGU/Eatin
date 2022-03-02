package controllers

import play.api.mvc.{Request, WrappedRequest}

import scala.concurrent.Future

case class RequestWithUserInfo[A](
                                   userid: Future[Long],
                                   request: Request[A]
                              ) extends WrappedRequest[A](request)
