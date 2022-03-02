import play.api.mvc.Results.Unauthorized
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, BodyParsers, Request, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

package object controllers {
  val HEADER_USERID = "userid"
  val SESSION_TOKEN_NAME = "token"
    class ProtectedAction @Inject() (
      val defaultParser : BodyParsers.Default)
                                    (implicit ec: ExecutionContext)
    extends ActionBuilder[RequestWithUserInfo,AnyContent] {
      override def parser: BodyParser[AnyContent] = defaultParser

      override def invokeBlock[A](request: Request[A], block: RequestWithUserInfo[A] => Future[Result]): Future[Result] = {
        val useridOpt = request.headers.get(HEADER_USERID).map(_.toLong)
        val tokenOpt = request.cookies.get(SESSION_TOKEN_NAME).map(_.value)
        if (useridOpt.isEmpty || tokenOpt.isEmpty) Future.successful(Unauthorized("Need login"))
        else {
          block(RequestWithUserInfo(Future(useridOpt.get), request))
        }
      }

      override protected def executionContext: ExecutionContext = ec
    }
}
