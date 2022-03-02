package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Cookie}
import repository.{ReservationRepository, UserRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{ExecutionContext, Future}

case class makeResReq(userid: Long, restId: Long, datetime: String)
case class checkAvailReq(restId: Long, datetime: String)

@Singleton
class ReservationController @Inject() (val reserveRepo: ReservationRepository,
                                       val authAction: ProtectedAction,
                                       implicit val cc: ControllerComponents)
                               (implicit val ec: ExecutionContext) extends AbstractController(cc) {
  implicit val makeResReqReads  = Json.reads[LoginReq]
  implicit val signUpReqReads = Json.reads[SignUpReq]
  def makeReservation(): Action[AnyContent] = {
      ???
    
  }
}
