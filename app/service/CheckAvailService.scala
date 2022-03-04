package service

import models.Restaurant
import repository.{OpenHoursRepository, ReservationRepository, RestaurantRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import scalacache._
import scalacache.guava._
import scalacache.memoization._
import scala.concurrent.duration._
import scalacache.modes.scalaFuture._
import com.google.common.cache.CacheBuilder


class CheckAvailService @Inject()(restRepo: RestaurantRepository,
                                  reserveRepo: ReservationRepository,
                                  openHoursRepo: OpenHoursRepository)(implicit ec: ExecutionContext){
  val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[Boolean]]
  implicit val guavaCache: Cache[Boolean] = GuavaCache(underlyingGuavaCache)
  def checkAvail(restId: Long, datetime: String): Future[Boolean] =
    memoizeF[Future, Boolean](Some(10.seconds)) {
      restRepo.searchByRestId(restId).flatMap {
        case restOpt: Some[Restaurant] =>
          val rest = restOpt.get
          openHoursRepo.searchByOpenId(rest.openId).flatMap {
            openHoursOpt => {
              reserveRepo.countConflict(restId, datetime).flatMap { count =>
                val oh = openHoursOpt.get
                val time = datetime.split(" ")(1)
                val open: Boolean = (oh.lunch && time >= oh.lStart && time < oh.lEnd) || (oh.dinner && time >= oh.dStart && time < oh.dEnd)
                val emptyTable = count < rest.tables
                Future.successful(open && emptyTable)
              }
            }
          }
        case None => Future.successful(false)
      }
    }



//    for {
//      openHours <- openHoursRepo.searchByRestId(restId)
//      countConflict <- reserveRepo.countConflict(restId, datetime)
//      rest <- restRepo.searchByRestId(restId)
//    } yield {
//      if (rest.isEmpty || openHours.isEmpty || (!openHours.get.dinner && openHours.get.lunch)) return Future.successful(false)
//      val oh = openHours.get
//      val r = rest.get
//      val open: Boolean = (oh.lunch && datetime >= oh.lStart && datetime < oh.lEnd) || (oh.dinner && datetime >= oh.dStart && datetime < oh.dEnd)
//      val emptyTable = countConflict < r.tables
//      open && emptyTable
//    }

}
