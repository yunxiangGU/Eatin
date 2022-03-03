package repository

import models.Reservation
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReservationRepository @Inject()(implicit ex: ExecutionContext) {
  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mydb")

  import dbConfig._
  import profile.api._ // brings slick DSL

  // Define Reservation Table
  private class ReservationTable(tag: Tag) extends Table[Reservation](tag, "reservation") {
    def reserveId = column[Long]("reserve_id", O.PrimaryKey, O.AutoInc)

    def datetime = column[String]("datetime")

    def restId = column[Long]("rest_id")

    def userid = column[Long]("userid")

    def duration = column[Int]("duration")

    def status = column[Int]("status")

    /*
    table default projection
    <>: projection
     */
    override def * = (reserveId, datetime, restId, userid, duration, status) <> ((Reservation.apply _).tupled, Reservation.unapply)
  }

  /**
   * Queries
   */
  private val restQuery = TableQuery[ReservationTable]

  /**
   * Write:

addReservation
updateDesc
updateName
Read:

searchReservationById
searchReservationByName
searchReservationByAddress
searchReservationByType
   */
  def addReservation(userid: Long, restId: Long, dateTime: String, duration: Int, status: Int): Future[Reservation] = {
    db.run {
      (restQuery.map(r => (r.datetime, r.restId, r.userid, r.duration, r.status))
        returning restQuery.map(_.reserveId)
        into ((reserveInfo, reserveId) => Reservation(reserveId, reserveInfo._1, reserveInfo._2, reserveInfo._3, reserveInfo._4, reserveInfo._5))
        ) += (dateTime, restId, userid, duration, status)
    }
  }

  def updateStatus(reserveId: Long, newStatus: Int): Future[Int] = {
    db.run(
      restQuery.filter(_.reserveId === reserveId).map(_.status).update(newStatus)
    )
  }

  def searchByReserveId(reserveId: Long): Future[Option[Reservation]] = {
    db.run {
      restQuery.filter(_.reserveId === reserveId).result.map( rSet =>
        rSet.headOption.map(
          r => Reservation(r.reserveId, r.dateTime, r.restId, r.userid, r.duration, r.status)
        ))
    }
  }
  def searchByUserid(userid: Long): Future[Option[Reservation]] = {
    db.run {
      restQuery.filter(_.userid === userid).result.map( rSet =>
        rSet.headOption.map(
          r => Reservation(r.reserveId, r.dateTime, r.restId, r.userid, r.duration, r.status)
        ))
    }
  }

  def searchByRestId(restId: Long): Future[Option[Reservation]] = {
    db.run {
      restQuery.filter(_.restId === restId).result.map( rSet =>
        rSet.headOption.map(
          r => Reservation(r.reserveId, r.dateTime, r.restId, r.userid, r.duration, r.status)
        ))
    }
  }
  // todo check avail should be inside service method which injecting 2 repo!!
  def countConflict(restId: Long, startTime: String): Future[Int] = {
    db.run {
      restQuery.filter(_.restId === restId).filter(_.datetime like startTime).length.result
    }
  }
}

