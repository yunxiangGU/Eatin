package filter

import akka.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

class LoginFilter extends Filter{
  override implicit def mat: Materializer = ???

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = ???
}
