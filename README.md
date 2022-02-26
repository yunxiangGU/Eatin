# Eatin
IT5100A Course Project

## Database:


1. User

```sql
CREATE TABLE `eatin`.`user` (
  `userid` INT NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `type` VARCHAR(45) NULL,
  PRIMARY KEY (`userid`),
  INDEX `username` (`username` ASC) VISIBLE);

```

2. Restaurant

```sql
CREATE TABLE `eatin`.`restaurant` (
  `rest_id` INT NOT NULL AUTO_INCREMENT,
  `userid` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `desc` VARCHAR(1024) NULL,
  `address` VARCHAR(256) NULL,
  `phone` VARCHAR(45) NULL,
  `open_id` INT NOT NULL,
  `tables` INT NOT NULL,
  `rest_type` VARCHAR(45) NULL,
  PRIMARY KEY (`rest_id`, `userid`));

```

3. Open hours:

```sql
CREATE TABLE `eatin`.`open_hours` (
  `open_id` INT NOT NULL,
  `breakfast` TINYINT NOT NULL,
  `lunch` TINYINT NOT NULL,
  `dinner` TINYINT NOT NULL,
  `b_start` TIME NULL,
  `b_end` TIME NULL,
  `l_start` TIME NULL,
  `l_end` TIME NULL,
  `d_start` TIME NULL,
  `d_end` TIME NULL,
  PRIMARY KEY (`open_id`));

```



4. Reservation

```sql
CREATE TABLE `eatin`.`reservation` (
  `reserve_id` INT NOT NULL,
  `datetime` DATETIME NOT NULL,
  `rest_id` INT NOT NULL,
  `userid` INT NOT NULL,
  `duration` TIME NOT NULL DEFAULT '01:00:00',
  `status` TINYINT NOT NULL, 
  PRIMARY KEY (`reserve_id`, `date`, `rest_id`, `userid`));

```

## Repo
### UserRepository:  
Write:
- addUser
- updatePassword
- updateEmail: implement send-Email functionality 

Read: 
- searchUserByUserid


### RestaurantRepository:
Write:
- addRestaurant
- updateDesc
- updateName  

Read:
- searchRestaurantById
- searchRestaurantByName
- searchRestaurantByAddress
- searchRestaurantByType

### OpenHoursRepository:
- addOpenHours
- searchOpenHoursByRestId
###ReservationRepository:  
Write:  
- insertReservation: insertReservation to table
- cancelReservation: status=0

Read:  
- searchReservationByUserid
- searchReservationByRestId
- searchReservationByDatetime
- checkAvailability: count(start_time<=x && end_time>=x && status=1) < tables

## Service
### ReservationService:
- addReservation: logic before make reservation
### UserService:
- if enough time: verify email
- if enough time: authentication
- if enough time: Encryption Utils (password, salt, encrypt)

### ReservationService:
- if enough time: batch addReservation from csv file for restaurants with their own service/database

## Controller
### TODO
