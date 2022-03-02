package models

import play.api.libs.json.Json

case class OpenHours(
                    openId: Long,
                    lunch: Boolean,
                    dinner: Boolean,
                    lStart: String,
                    lEnd: String,
                    dStart: String,
                    dEnd: String
                    )
object OpenHours {
  def tupled = (OpenHours.apply _).tupled
  implicit val OpenHoursFormat = Json.format[OpenHours]
}
