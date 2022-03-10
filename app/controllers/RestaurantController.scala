package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Cookie}
import repository.{OpenHoursRepository, RestaurantRepository, UserRepository}
import service.CheckAvailService
import utils.JsonUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{ExecutionContext, Future}

/**
 * request case class to add a new restaurant
 * @param name restaurant name
 * @param desc restaurant description
 * @param address restaurant address
 * @param phone restaurant phone number
 * @param lunch restaurant is open for lunch or not
 * @param lStart time of lunch start e.g. 12:00:00
 * @param lEnd time of lunch end e.g. 15:00:00
 * @param dinner restaurant is open for dinner or not
 * @param dStart time of dinner start
 * @param dEnd time of dinner end
 * @param tables number of tables in restaurant ( for online reservation)
 * @param restType type of restaurant: like 'asian', 'chinese', 'japanese', 'western'
 */
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
          .getOrElse(throw new IllegalArgumentException("illegal request for add restaurant"))
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

  /**
   * get restaurant list for display in home page
   * @return
   */
  def getRestList: Action[AnyContent] = { Action.async { request =>
    restaurantRepository.getRestList.flatMap { list =>
      Future.successful(Ok(JsonUtil.toJson(list)))
    }
  }
  }

  /**
   * support customised search
   * @param name search by restaurant name
   * @return
   */
  def searchRestaurantByName(name: String): Action[AnyContent] = { Action.async { request =>
    println(name)
    restaurantRepository.searchByName(name).flatMap {
      restList => Future.successful(Ok(JsonUtil.toJson(restList)))
    }.recover {
      case _: Throwable => InternalServerError("uncaught error")
    }
  }
  }

  /**
   * support customized search by address
   */
  def searchRestaurantByAddress(address: String): Action[AnyContent] = {Action.async { request =>
    restaurantRepository.searchByAddress(address).flatMap { restList =>
      Future.successful(Ok(JsonUtil.toJson(restList)))
    }.recover {
      case _: Throwable => InternalServerError("uncaught error")
    }
  }
  }

  def searchRestaurantByRestType(restType: String): Action[AnyContent] = {Action.async { request =>
    restaurantRepository.searchByType(restType).flatMap { restList =>
      Future.successful(Ok(JsonUtil.toJson(restList)))
    }.recover {
      case _: Throwable => InternalServerError("uncaught error")
    }
  }
  }

  /**
   * support general search: search by name, address, and type & combine the result
   * @param keyword any related keyword
   * @return
   */
  def searchRestaurantGenerally(keyword: String): Action[AnyContent] = {Action.async { request =>
    println(keyword)
    for {
      nameList <- restaurantRepository.searchByName(keyword)
      addressList <- restaurantRepository.searchByAddress(keyword)
      typeList <- restaurantRepository.searchByType(keyword)
    } yield {
      val res = nameList ++ addressList ++ typeList
      Ok(JsonUtil.toJson(res))
    }
  }}

  /**
   * below is for:
   * support for restaurant user
   */
  def searchMyRestaurant(): Action[AnyContent] = {authAction.async{ request =>
    request.userid.flatMap { userid =>
      val lst = restaurantRepository.searchByUserId(userid).map(x => x)
      Future.successful(Ok(JsonUtil.toJson(
        lst
      )))
    }
  }}
}
