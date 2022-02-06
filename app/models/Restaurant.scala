package models

import play.api.libs.json.Json

case class Restaurant(
                     restId: Long,
                     userid: Long,
                     name: String,
                     desc: String,
                     address: String,
                     phone: String,
                     openId: Long,
                     tables: Int,
                     restType: String
                     )
object Restaurant {
  implicit val RestaurantFormat = Json.format[Restaurant]
}
