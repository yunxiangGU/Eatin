package models

import play.api.libs.json.Json

case class User(userId: Long, username: String, password: String, email: String, userType: String)

object User {
  implicit val UserFormat = Json.format[User]
}
