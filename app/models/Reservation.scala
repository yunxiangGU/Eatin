package models

import akka.http.scaladsl.model.DateTime
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

case class Reservation(
                        reserveId: Long,
                        dateTime: DateTime,
                        restId: Long,
                        userid: Long,
                        duration: Duration, // TBD
                        status: Int
                      )
object Reservation {
  implicit val ReservationFormat = Json.format[Reservation]
}
