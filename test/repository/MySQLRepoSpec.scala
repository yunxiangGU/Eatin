package repository

import scala.concurrent.ExecutionContext

import akka.Done
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.inject.guice.GuiceApplicationBuilder
import slick.jdbc.JdbcProfile

trait MySQLRepoSpec  extends PlaySpec
  with GuiceOneAppPerSuite
  with ScalaFutures
  with BeforeAndAfterAll
  with HasDatabaseConfigProvider[JdbcProfile]{
  implicit lazy val executor: ExecutionContext = fakeApplication.actorSystem.dispatcher

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(1000, Millis))

  protected override lazy val dbConfigProvider: DatabaseConfigProvider =
    fakeApplication.injector.instanceOf[DatabaseConfigProvider]

  override lazy val fakeApplication: Application =
    new GuiceApplicationBuilder()
      .configure(Map(
        "mydb.profile" -> "slick.jdbc.MySQLProfile$",
        "mydb.db.driver" -> "com.mysql.jdbc.Driver",
        "mydb.db.url" -> "jdbc:mysql://127.0.0.1:3306/eatin?useSSL=false&",
        "mydb.db.user" -> "root",
        "mydb.db.password" -> "lianzi89"
      ))
      .build()

  override def afterAll(): Unit = {
    val _ = fakeApplication.stop().mapTo[Done].futureValue
    ()
  }
}
