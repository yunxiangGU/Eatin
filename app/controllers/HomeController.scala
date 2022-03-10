package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import repository.UserRepository

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.{Duration, SECONDS}
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val userRepository: UserRepository,
                                val controllerComponents: ControllerComponents) (implicit ec: ExecutionContext)extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok("yeah")
  }
//
//  def addUser() = Action { implicit request: Request[AnyContent] =>
//    val res = userRepository.addUser("1", "2", "3", "4")
//    Await.result(res, Duration(60,SECONDS))
//    Ok("OK")
//  }
}
