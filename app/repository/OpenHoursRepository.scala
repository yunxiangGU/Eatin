package repository

import models.OpenHours
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenHoursRepository @Inject()(configProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext) {
  //private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mydb")
  private val dbConfig: DatabaseConfig[JdbcProfile] = configProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._ // brings slick DSL

  // Define OpenHours Table
  private class OpenHoursTable(tag: Tag) extends Table[OpenHours](tag, "open_hours") {
    def openId = column[Long]("open_id", O.PrimaryKey, O.AutoInc)

    def lunch = column[Boolean]("lunch")

    def dinner = column[Boolean]("dinner")

    def lStart = column[String]("l_start")

    def lEnd = column[String]("l_end")

    def dStart = column[String]("d_start")

    def dEnd = column[String]("d_end")

    /*
    table default projection
    <>: projection
     */
    override def * =
      (openId, lunch, dinner, lStart, lEnd, dStart, dEnd) <> (OpenHours.tupled, OpenHours.unapply)
  }

  /**
   * Queries
   */
  private val openQuery = TableQuery[OpenHoursTable]

  /**
   * Write:
  */
  def addOpenHours(lunch: Boolean, dinner: Boolean, lStart: String, lEnd: String, dStart: String, dEnd: String): Future[OpenHours] = {
    db.run {
      (openQuery.map(o => (o.lunch, o.dinner, o.lStart, o.lEnd, o.dStart, o.dEnd))
        returning openQuery.map(_.openId)
        into ((openInfo, openId) => OpenHours(openId, openInfo._1, openInfo._2, openInfo._3, openInfo._4, openInfo._5, openInfo._6)
        )) += (lunch, dinner, lStart, lEnd, dStart, dEnd)
    }
  }

  def searchByOpenId(openId: Long): Future[Option[OpenHours]] = {
    db.run {
      openQuery.filter(_.openId === openId).result.map( rSet =>
        rSet.headOption.map(
          r => OpenHours(r.openId, r.lunch, r.dinner, r.lStart, r.lEnd, r.dStart, r.dEnd)
        ))
    }
  }
}

