import play.api.mvc.Results.Unauthorized
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, BodyParsers, Request, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

package object controllers {
  val SESSION_TOKEN_NAME = "token"
    class ProtectedAction @Inject() (
      val defaultParser : BodyParsers.Default)
                                    (implicit ec: ExecutionContext)
    extends ActionBuilder[RequestWithUserInfo,AnyContent] {
      override def parser: BodyParser[AnyContent] = defaultParser

      override def invokeBlock[A](request: Request[A], block: RequestWithUserInfo[A] => Future[Result]): Future[Result] = {
        val useridOpt = request.session.get(SESSION_TOKEN_NAME).map(_.toLong)
        if (useridOpt.isEmpty) Future.successful(Unauthorized("Need login"))
        else {
          block(RequestWithUserInfo(Future(useridOpt.get), request))
        }
      }

      override protected def executionContext: ExecutionContext = ec
    }
}
