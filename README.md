# Eatin
IT5100A Course Project

## Database:


1. User

```sql
CREATE TABLE `eatin`.`user` (
  `userid` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) UNIQUE NOT NULL,
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
  `open_id` INT NOT NULL AUTO_INCREMENT ,
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
  `reserve_id` INT NOT NULL AUTO_INCREMENT,
  `datetime` DATETIME NOT NULL,
  `rest_id` INT NOT NULL,
  `userid` INT NOT NULL,
  `duration` INT NOT NULL DEFAULT 1,
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
### ReservationRepository:  
Write:  
- insertReservation: insertReservation to table
- cancelReservation: status=0

Read:  
- searchReservationByUserid
- searchReservationByRestId
- searchReservationByDatetime
- checkAvailability: 
```
count(start_time<=x && end_time>=x && status=1) < tables // exist empty table(s)
&&
openhours.lunchStart <= x < openHours.lunchEnd || openhours.dinnerStart <= x < openHhours.dinnerEnd
// in restaurant open hours
```


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
#### BackEnd API:
##### Sign up: 
- path: /signup
- method: POST
- Json body: {"username": "abc", "password":"123", "email":"abc@gmail.com"}
- Success response: "success"

##### Login:
- path: /login
- method: POST
- request Json body: {"username":"abc", "password":"123"}
- Success response: "success"
- Incorrect username/password: "incorrect username/password"
##### logout
- path: /logout
- method: POST
- "logout success"

##### updatePassword
- path: /updatePassword
- Need Auth session
- method: POST
- request Json body: {"oldPassword":"123", "newPassword":"456"}
- "success"

##### createRestaurant
- path: /addRest
- Need Auth session
- method: POST
- request Json body: 
```json
{
    "name": "happy family",
    "desc": "Asian food for family gathering",
    "address": "388 PeekWay",
    "phone":"86259087",
    "lunch": true,
    "lStart": "11:00",
    "lEnd": "14:00",
    "dinner": true,
    "dStart": "15:00",
    "dEnd": "20:00",
    "tables":20,
    "restType":"Asian"
    }
```
- response Json if success
```json
{"restId":2,
  "userid":4,
  "openId":2,
  "name":"happy family",
  "desc":"Asian food for family gathering",
  "address":"388 PeekWay",
  "phone":"86259087",
  "tables":20,
  "restType":"Asian"}
```
##### get Restaurant List
- path: /getRestList
- method: GET
- response is 10(or less than 10) restaurant information list
- response sample Json:
```json
[{"restId":1,"userid":1,"openId":1,"name":"wokhey","desc":"goodrice","address":"clementi","phone":"12345678","tables":2,"restType":"asian"},
  {"restId":2,"userid":4,"openId":2,"name":"happy family","desc":"Asian food for family gathering","address":"388 PeekWay","phone":"86259087","tables":20,"restType":"Asian"}]
```

##### Check Availability
- path: /check
- method: POST
- Need Auth session (TBD)
- request Json body:
```json
{
    "restId": 1,
    "datetime":"2022-2-22 12:00:00"
}
```
- response: true or false

##### Make Reservation
- path: /addRest
- method: POST
- need Auth
- request Json body:
```json
{
    "restId": 1,
    "datetime":"2022-3-22 12:00:00"
}
```

## UI run
```shell
npm install
```

```shell
npm run start
```
