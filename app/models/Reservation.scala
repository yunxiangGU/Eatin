package models

import akka.http.scaladsl.model.DateTime
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.duration.Duration

case class Reservation(
                        reserveId: Long,
                        dateTime: String,
                        restId: Long,
                        userid: Long,
                        duration: Int, // TBD
                        status: Int
                      )
object Reservation {
  implicit val ReservationFormat: OFormat[Reservation] = Json.format[Reservation]
}
