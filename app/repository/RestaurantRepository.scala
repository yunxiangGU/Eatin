package repository

import models.Restaurant
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class RestaurantRepository @Inject()(configProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext) {
  //private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mydb")
  private val dbConfig: DatabaseConfig[JdbcProfile] = configProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._ // brings slick DSL

  // Define Restaurant Table
  private class RestaurantTable(tag: Tag) extends Table[Restaurant](tag, "restaurant") {
    def restId = column[Long]("rest_id", O.PrimaryKey, O.AutoInc)

    def userid = column[Long]("userid")

    def openId = column[Long]("open_id")

    def name = column[String]("name")

    def desc = column[String]("desc")

    def address = column[String]("address")

    def phone = column[String]("phone")

    def tables = column[Int]("tables")

    def restType = column[String]("rest_type")

    /*
    table default projection
    <>: projection
     */
    override def * = (restId, userid, openId, name, desc, address, phone, tables, restType) <> ((Restaurant.apply _).tupled, Restaurant.unapply)
  }

  /**
   * Queries
   */
  private val restQuery = TableQuery[RestaurantTable]

  /**
   * Write:

    addRestaurant
    updateDesc
    updateName
    Read:

    searchRestaurantById
    searchRestaurantByName
    searchRestaurantByAddress
    searchRestaurantByType
   */
  def addRestaurant(userid: Long, openId: Long, name: String, desc: String, address: String, phone: String, tables: Int, restType: String): Future[Restaurant] = {
    db.run {
      (restQuery.map(r => (r.userid, r.openId, r.name, r.desc, r.address, r.phone, r.tables, r.restType))
        returning restQuery.map(_.restId)
        into ((restInfo, restId) => Restaurant(restId, restInfo._1, restInfo._2, restInfo._3, restInfo._4, restInfo._5, restInfo._6, restInfo._7, restInfo._8))
        ) += (userid, openId, name, desc, address, phone, tables, restType)
    }
  }

  def updateDesc(restId: Long, newDesc: String): Future[Int] = {
    db.run(
      restQuery.filter(_.restId === restId).map(_.desc).update(newDesc)
    )
  }

  def updateName(restId: Long, newName: String): Future[Int] = {
    db.run(
      restQuery.filter(_.restId === restId).map(_.name).update(newName)
    )
  }
  def getRestList: Future[List[Restaurant]] = {
    db.run(
      restQuery.take(10).result.map{ rSet =>
        rSet.toList
      }
    )
  }

  def searchByRestId(restId: Long): Future[Option[Restaurant]] = {
    db.run {
      restQuery.filter(_.restId === restId).result.map( rSet =>
      rSet.headOption.map(
        r => Restaurant(r.restId, r.userid, r.openId, r.name, r.desc, r.address, r.phone, r.tables, r.restType)
      ))
    }
  }
  def searchByName(name: String): Future[List[Restaurant]] = {
    db.run {
      restQuery.filter(_.name.toLowerCase like s".*${name}.*".toLowerCase()).take(5).result.map {
          _.toList
        }
    }
  }

  def searchByAddress(address: String): Future[List[Restaurant]] = {
    db.run {
      restQuery.filter(_.address.toLowerCase like s".*${address}.*".toLowerCase() ).result.map(_.toList)
    }
  }

  def searchByType(restType: String): Future[List[Restaurant]] = {
    db.run {
      restQuery.filter(_.restType.toLowerCase like s".*${restType}.*".toLowerCase() ).result.map(_.toList)
    }
  }
  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

}
