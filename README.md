# Eatin
IT5100A Course Project
## Contribution

| contribution                              | member                   |
| ----------------------------------------- | ------------------------ |
| Design                                    | Huang Yifan, Gu Yunxiang |
| Repositories (Slick & Database Interface) | Huang Yifan              |
| Models and Controllers                    | Huang Yifan              |
| Front End, React Components               | Gu Yunxiang              |
| Cache                                     | Han Siyu                 |
| Report                                    | Huang Yifan, Gu Yunxiang |



## Background

Eatin is an online restaurant reservation, which aims to offer both customers and restaurants a place to manage their reservations. On customer side, they can search, filter, and make bookings simply on the same web page. On merchant side, they are able to utilise our management system to better organise their reservation data.

Since this is a classic web application, we are using Play framework with Scala to organise our projects. In terms of data storage, we are going to use MySql database for long-lasting data, and Redis for caching availability data. For front-end, we are using React framework to make best use of existing libraries and better encapsulate components.

Because the time is limited for our project, we have implemented most of the basic features above while leaving rest of them for future.

## Run Eatin!

We chose to focus on back-end while also offer some front-end features implemented by React. To start this project, you should first clone this repository from https://github.com/yunxiangGU/Eatin:

```sh
git clone https://github.com/yunxiangGU/Eatin.git
cd Eatin
```

The project structure is classic Play structure with a front-end `ui` folder. To start the web application, back-end and front-end services should both be started successfully. In addition, we use MySQL as persistent storage so the corresponding database and tables need to be initialised as **Appendix(a)** described. To connect the local database successfully, the following part of `conf/application.conf` need to be modified to valid values:

```scala
slick.dbs.default.db.user="root"
slick.dbs.default.db.password="pass"
```

To start backend, use `sbt` (`Java version 8 or 11`).

```sh
sbt compile
sbt run
```

To start frontend, `npm` required.

```sh
npm install
npm run start
```

By default, back end will start on port 9000, while front end will start on port 3000.

## Implementation Details

## a. Database:


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

## b.Repo
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


## c. Service
### ReservationService:
- addReservation: logic before make reservation
### UserService:
- if enough time: verify email
- if enough time: authentication
- if enough time: Encryption Utils (password, salt, encrypt)

### ReservationService:
- if enough time: batch addReservation from csv file for restaurants with their own service/database

## d. Controller
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

##### Search Restaurants

- Path:/search?keyword=xxx
- Method: GET
- Response sample Json

```json
[{"restId":1,"userid":1,"openId":1,"name":"wokhey","desc":"goodrice","address":"clementi","phone":"12345678","tables":2,"restType":"asian"},
  {"restId":2,"userid":4,"openId":2,"name":"happy family","desc":"Asian food for family gathering","address":"388 PeekWay","phone":"86259087","tables":20,"restType":"Asian"}]
```

##### Search Restaurants with criteria

- Path1: /searchByName?name=xxx
- Path2: /searchByAddress?address=xxx
- Path3: /searchByType?type=xxx
- Method: GET
- Sample response Json is same as search by keywords. 

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
### Front End Implementation

We use `React` library for implementing web front end. Our prototype website has five webpages, navigation implemented with `react router`, to be scaled in the future.

- Home Page
- Sign In Page
- Sign In Success Page
- Sign Up Page
- Sign Up Success Page

Users would expect to be landed on home page, where there will be two buttons: sign up, and sign in, on the header bar. Each button will take the user to the corresponding page.

On Sign In Page, users will be prompted to enter `username` and `password` to sign in. 

If successful, users will be redirected to the Sign In Successful Page, where a `welcome back` message will be displayed.

Also, we prompted users to click on the Xiao Long Bao icon to go back to home page.

Sign Up Page is quite similar to Sign In Page, but with an extra field to provide the user's `email` other than `username` and `password`. The message upon success sign up is `welcome`, instead of `welcome back`.

On home page, users can perform search based on their conditions: dining time, number of pax, and keyword search. Now only keyword search is implemented on front end, but the back end logic is complete. When users type in keywords (e.g., 'asian', or '100 Orchard Road', or 'Wok Hey'), we send a `get` request to backend with `keyword` parameters in URL. The back end will respond with an array of all restaurants (up to 10 records) meeting the filter requirement. Then all these result restaurants will be displayed at the bottom of the home page as several `RestaurantCard` components, which are scrollable horizontally.



## Future Plan

Front End (some functionalities existing in back end but pending on front end): 

- More search filtering logic in dining time and number of pax
- users should be able to add their own business (restaurant), along with information (address, cuisine type, phone number, number of available tables, etc.), since they could be business owners other than customers
- users should be able to click on a `RestaurantCard` component and be redirected to the detailed info page about that restaurant
- on that detail page, users should be able to make reservations for that restaurant
- users should be able to update their information (password, email, etc.) after successful login
- restaurant owners should be able to update their restaurant information (name, description, etc.) after successful login
## UI run
```shell
npm install
```

```shell
npm run start
```
