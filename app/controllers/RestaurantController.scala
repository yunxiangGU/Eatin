package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Cookie}
import repository.{OpenHoursRepository, RestaurantRepository, UserRepository}
import service.CheckAvailService
import utils.JsonUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{ExecutionContext, Future}

case class AddRestReq(name: String, desc: String, address: String, phone: String, lunch: Boolean, lStart: String, lEnd: String, dinner: Boolean, dStart: String, dEnd: String, tables: Int, restType: String)

@Singleton
class RestaurantController @Inject() (val restaurantRepository: RestaurantRepository,
                                      val openHoursRepo: OpenHoursRepository,
                                       val checkAvailService : CheckAvailService,
                                       val authAction: ProtectedAction,
                                       implicit val cc: ControllerComponents)
                                      (implicit val ec: ExecutionContext) extends AbstractController(cc) {
  implicit val addRestReq  = Json.reads[AddRestReq]
  def addRestaurant(): Action[AnyContent] = { authAction.async { request =>
    request.userid.flatMap {
      userid =>
        val reqJson = request.body.asJson
          .getOrElse(throw new IllegalArgumentException("illegal request for login"))
        val addReq = reqJson.asOpt[AddRestReq].getOrElse(throw new IllegalArgumentException("illegal request for login"))
        openHoursRepo.addOpenHours(addReq.lunch, addReq.dinner, addReq.lStart, addReq.lEnd, addReq.dStart, addReq.dEnd).flatMap { oh =>
          val openId = oh.openId
          restaurantRepository.addRestaurant(userid, openId, addReq.name, addReq.desc, addReq.address, addReq.phone, addReq.tables, addReq.restType).flatMap {
            rest => {
              Future.successful(Ok(JsonUtil.toJson(rest)))
            }
          }
        }
    }
      .recover {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
        case t: Throwable => InternalServerError(t.getMessage)
      }
  }
  }

  def getRestList: Action[AnyContent] = { Action.async { request =>
    restaurantRepository.getRestList.flatMap { list =>
      restaurantRepository.close()
      Future.successful(Ok(JsonUtil.toJson(list)))
    }
  }
  }


}
