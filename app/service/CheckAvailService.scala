package service

import repository.{ReservationRepository, RestaurantRepository}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckAvailService @Inject()(restRepo: RestaurantRepository, reserveRepo: ReservationRepository)(implicit ec: ExecutionContext){
  def checkAvail(restId: Long, datetime: String): Future[Boolean] = {
    for {
      rest <- restRepo.searchByRestId(restId)
      conflictNo <- reserveRepo.countConflict(restId, datetime)
    } yield rest.get.tables > conflictNo
  }
}
