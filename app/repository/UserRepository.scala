package repository

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import java.sql.ResultSet
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}

@Singleton
class UserRepository @Inject()(implicit ex: ExecutionContext){
  private val dbConfig : DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mydb")
  import dbConfig._
  import profile.api._ // brings slick DSL

  // Define User Table
  private class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def userid = column[Long] ("userid", O.PrimaryKey, O.AutoInc)

    def username = column[String]("username")

    def password = column[String]("password")

    def email = column[String]("email")

    def userType = column[String]("user_type")

    /*
    table default projection
    <>: projection
     */
    override def * = (userid, username, password, email, userType) <> ((User.apply _).tupled, User.unapply)

  }

  /**
   * Queries
   */
  private val userQuery = TableQuery[UserTable]

  def addUser(username: String, password: String, email: String, userType: String): Future[User] = {
    db.run {
      (userQuery.map(u => (u.username, u.password, u.email, u.userType))
        returning userQuery.map(_.userid)
        into ((userInfo, userid) => User(userid, userInfo._1, userInfo._2, userInfo._3, userInfo._4))
        ) += (username, password, email, userType)
    }
  }
  // val updateCountryAction = playerTable.filter(_.id === 500L).map(_.country).update("Germany")
  def updatePassword(userid: Long, newPassword: String) = {
    db.run{
      userQuery.filter(_.userid === userid).map(_.password).update(newPassword)
    }
  }
}

