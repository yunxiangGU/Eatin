package repository
import models.User
import org.scalatest.WordSpec
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.{GuiceOneAppPerSuite, GuiceOneAppPerTest}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import play.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class UserRepositoryTest extends MySQLRepoSpec {
  var existGroupId = 0

  private val repository = fakeApplication.injector.instanceOf[UserRepository]

//  override def beforeAll(): Unit = {
//    existGroupId =
//      db.run((groups returning groups.map(_.id)) += Group(None, "test-group")).futureValue
//    ()
//  }
//
//  override def afterAll(): Unit = {
//    val _ = db.run(users.delete andThen groups.delete).futureValue
//
//    super.afterAll()
//  }
  def userRepo(implicit app: Application) = Application.instanceCache[UserRepository].apply(app)
  "User repo" should {
    "add a new user" in {
      val result: User = Await.result(userRepo.addUser(
        "yifan", "123", "gmail", "admin")
        , Duration(60, SECONDS))
      result.userType should equal("admin")
    }
  }


}
