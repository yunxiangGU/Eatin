package models

import play.api.libs.json.Json

case class OpenHours(
                    openId: Long,
                    breakfast: Boolean,
                    lunch: Boolean,
                    dinner: Boolean,
                    bStart: String,
                    bEnd: String,
                    lStart: String,
                    lEnd: String,
                    dStart: String,
                    dEnd: String
                    )
object OpenHours {
  implicit val OpenHoursFormat = Json.format[OpenHours]
}
