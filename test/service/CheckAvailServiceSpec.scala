package service

import models.User
import play.api.Application
import repository.{MySQLRepoSpec, OpenHoursRepository, ReservationRepository, RestaurantRepository, UserRepository}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class CheckAvailServiceSpec extends MySQLRepoSpec {
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
  def restRepo(implicit app: Application) = Application.instanceCache[RestaurantRepository].apply(app)
  def openHour(implicit app: Application) = Application.instanceCache[OpenHoursRepository].apply(app)
  def reserveRepo(implicit app: Application) = Application.instanceCache[ReservationRepository].apply(app)
  "check avail" should {
    val service = new CheckAvailService(restRepo, reserveRepo, openHour)
    "doesn't throw" in {
      val resF = service.checkAvail(1, "2022-02-22 12:00:00")
      val res = Await.result(resF, Duration(60, SECONDS))
      assertResult(false)(res)
    }
  }
  "reserveRepo" should {
    "not wrong" in {
      val resF = reserveRepo.countConflict(1, "2022-02-22 12:00:00")
      val res = Await.result(resF, Duration(60, SECONDS))
      assertResult(4)(res)
    }
  }
}

