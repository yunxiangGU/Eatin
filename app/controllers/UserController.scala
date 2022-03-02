package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, Cookie}
import repository.UserRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{DAYS, Duration}
import scala.concurrent.{ExecutionContext, Future}

case class LoginReq(username: String, password: String)
case class SignUpReq(username: String, password: String, email: String)

@Singleton
class UserController @Inject() (val userRepository: UserRepository,
                                val authAction: ProtectedAction,
                                implicit val cc: ControllerComponents)
                               (implicit val ec: ExecutionContext) extends AbstractController(cc) {
  implicit val loginReqReads  = Json.reads[LoginReq]
  implicit val signUpReqReads = Json.reads[SignUpReq]
    def login(): Action[AnyContent] = { Action.async(
      request => try {
        val reqJson = request.body.asJson
          .getOrElse(throw new IllegalArgumentException("illegal request for login"))
        val loginReq = reqJson.asOpt[LoginReq].getOrElse(throw new IllegalArgumentException("illegal request for login"))
        userRepository.searchUserByUsername(loginReq.username).flatMap {
          u => if (u.getOrElse(throw new IllegalArgumentException("no such user")).password == loginReq.password) {
            // todo: use random value as cookie value
            Future.successful(Ok("success").withCookies(Cookie(SESSION_TOKEN_NAME, "123", maxAge = Some(Duration(1, DAYS).toSeconds.toInt))))
          } else {
            Future.successful(Ok("incorrect userid/password"))
          }
        }
      } catch {
        case e: IllegalArgumentException => Future(BadRequest(e.getMessage))
        case t: Throwable =>
          println(t.getMessage)
          Future(InternalServerError("uncaught error"))
      }
    )
    }
  def signUp(): Action[AnyContent] = { Action.async {
    request => try {
      val reqJson = request.body.asJson.getOrElse(throw new IllegalArgumentException("illegal request for update password"))
      val signUpReq = reqJson.asOpt[SignUpReq].getOrElse(throw new IllegalArgumentException("invalid format of username"))
      /* Assume front end jQuery only allow unique username */
      userRepository.addUser(signUpReq.username, signUpReq.password, signUpReq.email, "normal")
      Future.successful(Ok("sign up success"))
    }
  }
  }
    def updatePassword(): Action[AnyContent] = { authAction.async {
      request => try {
        val reqJson = request.body.asJson.
          getOrElse(throw new IllegalArgumentException("illegal request for update password"))
        val oldPassword = reqJson("oldPassword").asOpt[String].getOrElse(throw new IllegalArgumentException("invalid format of oldPassword"))
        val newPassword = reqJson("newPassword").asOpt[String].getOrElse(throw new IllegalArgumentException("invalid format of newPassword"))
        val userid = reqJson("userid").asOpt[String].getOrElse(throw new IllegalArgumentException("invalid format of userid")).toLong
        userRepository.searchUserById(userid).flatMap {
          u => {
            if (u.getOrElse(throw new IllegalArgumentException("no such user")).password == oldPassword) userRepository.updatePassword(userid, newPassword)
            else throw new IllegalArgumentException("incorrect password")
            Future(Ok("success"))
          }
        }
      } catch {
        case e: IllegalArgumentException => Future(BadRequest(e.getMessage))
        case _: Throwable => Future(InternalServerError("uncaught error"))
      }
    }
    }
}
