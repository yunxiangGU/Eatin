package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Cookie}
import repository.{ReservationRepository, RestaurantRepository, UserRepository}
import service.CheckAvailService
import utils.JsonUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{ExecutionContext, Future}

case class MakeResReq(restId: Long, datetime: String)
case class CheckAvailReq(restId: Long, datetime: String)

@Singleton
class ReservationController @Inject() (val reserveRepo: ReservationRepository,
                                       val checkAvailService : CheckAvailService,
                                       val restaurantRepository: RestaurantRepository,
                                       val authAction: ProtectedAction,
                                       implicit val cc: ControllerComponents)
                               (implicit val ec: ExecutionContext) extends AbstractController(cc) {
  implicit val makeResReqReads  = Json.reads[MakeResReq]
  implicit val checkAvailReqReads = Json.reads[CheckAvailReq]
  def makeReservation(): Action[AnyContent] = { authAction.async { request =>
    request.userid.flatMap {
      userid =>
        val reqJson = request.body.asJson
          .getOrElse(throw new IllegalArgumentException("illegal request for login"))
        val makeResReq = reqJson.asOpt[MakeResReq].getOrElse(throw new IllegalArgumentException("illegal request for login"))
        reserveRepo.addReservation(userid, makeResReq.restId, makeResReq.datetime, 1, 0).flatMap {
          reservation => {
            Future.successful(Ok(reservation.dateTime))
          }
        }
    }
      .recover {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
        case t: Throwable => InternalServerError(t.getMessage)
      }
  }
  }
  def checkAvail() : Action[AnyContent] = { authAction.async { requestWithUserInfo =>
    requestWithUserInfo.userid.flatMap {
      userid =>
      val makeResReq = requestWithUserInfo.request.body.asJson.getOrElse(throw new IllegalArgumentException("bad make reservation request"))
        .asOpt[MakeResReq].getOrElse(throw new IllegalArgumentException("bad make reservation request"))
        checkAvailService.checkAvail(makeResReq.restId, makeResReq.datetime).map {
          case true => Ok(JsonUtil.toJson(true))
          case false => Ok(JsonUtil.toJson(false))
        }
    }
      .recover {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
        case t: Throwable => InternalServerError(t.getMessage)
      }
  }
  }
  def searchReservationByUserId(): Action[AnyContent] = authAction.async{request =>
    request.userid.flatMap { userid =>
      Future.successful(Ok(JsonUtil.toJson(reserveRepo.searchByUserid(userid))))
    }.recover {
      case t: Throwable => InternalServerError("unknown error")
    }
  }

  def cxlReservation(reserveId: Long): Action[AnyContent] = authAction.async { request =>
    request.userid.flatMap { userid => {
      reserveRepo.searchByUserid(userid).flatMap { reserveList =>
        reserveList.filter(_.reserveId == reserveId).map { r =>
          reserveRepo.updateStatus(r.reserveId, 1)
        }
        Future.successful(Ok("done"))
      }
    }
    }
  }
  /**
   * below is for:
   * support for restaurant user
   */
    def searchReservationByRestaurantId(restId: Long) :Action[AnyContent] = authAction.async {request =>
      request.userid.flatMap { userid =>
        val restLst = restaurantRepository.searchByUserId(userid)
        Future.successful(Ok(JsonUtil.toJson(restLst.map { lst =>
          lst.filter(_.restId == restId)
        })))
      }
    }


  def test() : Action[AnyContent] = {
    authAction.async { requestWithUserInfo =>
      requestWithUserInfo.userid.flatMap {
        userid =>
        reserveRepo.countConflict(1, "2021-10-09 10:00:00").flatMap {
          i => Future.successful(Ok(i.toString))
        }

      }
    }
    }

}
