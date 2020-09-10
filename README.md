# videoRental

## Assumptions and guidelines 

- The application will have a fully functional CRUD api for all entities  from the domain model. 
  Please look at the actual ERP diagram I have created for this app.
- All api endpoints for the app are secured using JWTokens. 
  By default I use liquibase migration tool in combination with faker.js to seed some data in the H2 with disc persistence db on dev profile.
  H2 db will be populated with 4 users(customer) and 10 movies. Customer is just an extension to the user (one to one) relationship.

  The most important one are admin and user. 
  
  Admin user credentials are:
  ```
  username: admin
  password: admin
  ```
    
  Basic user credentials are:
  ```
    username: user
    password: user
  ```
  
  Authentication endpoint is:
  
  ```
  POST /api/authenticate HTTP/1.1
  Host: localhost:8080
  Content-Type: application/json
  User-Agent: PostmanRuntime/7.16.3
  Accept: */*
  Cache-Control: no-cache
  Postman-Token: 8adc0586-1fa8-4864-a99f-e50346efe69e,11154f29-deaf-4dda-8939-876668470248
  Host: localhost:8080
  Accept-Encoding: gzip, deflate
  Content-Length: 37
  Connection: keep-alive
  cache-control: no-cache
  
  {"password":"user","username":"user"}
  
    ```
  This endpoint will return a jwtoken which will be used as an authentication bearer for all other API requests.
  
  ```
  GET /api/_search/movie-inventories?joker=deep HTTP/1.1
  Host: localhost:8080
  Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU5OTY0OTg5NX0.dIlrtTbc15T8-50nKtogTbHOKF7WKmHZtv_1InTUgL8Iudr5fD0P9TuB9JfAMlDu8MMygz83DqdZqUng-PNovw
  Content-Type: application/json
  User-Agent: PostmanRuntime/7.16.3
  Accept: */*
  Cache-Control: no-cache
  Postman-Token: 9edd66d2-701f-4b81-bedf-f1c07f61e14f,65c8d87e-ba5c-4d99-a69a-9b08dea4ca1a
  Host: localhost:8080
  Accept-Encoding: gzip, deflate
  Connection: keep-alive
  cache-control: no-cache
  ```
  
  - Admin users can create new users with default password: passpass. Users can crate profile on their own as well, 
  but they will need to be activated using the account api.
  
  - To document the API I use Swagger https://editor.swagger.io/. The following url could be loaded in an online swagger editor and the API with all endpoints and thair signature will be shown in the browser
  ```
  http://localhost:8080/v2/api-docs
  ```
  
- All business objects will expose an elastic search API to search by different attributes of an object.

- Users can rent one or many movies using this endpoint:
 ```
 POST /api/rental-orders HTTP/1.1
 Host: localhost:8080
 Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU5OTY0OTg5NX0.dIlrtTbc15T8-50nKtogTbHOKF7WKmHZtv_1InTUgL8Iudr5fD0P9TuB9JfAMlDu8MMygz83DqdZqUng-PNovw,Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTU5OTc4MTY5Nn0.Ht03bW0aapT9Tjh2n10XPZq7ogD_c83np9I0kiwgnYHAowat-7zmYzXcUREIBApaOlrIRaGUrQ8bsxLM0u555A
 Content-Type: application/json
 User-Agent: PostmanRuntime/7.16.3
 Accept: */*
 Cache-Control: no-cache
 Postman-Token: 49491499-9a6b-4aa2-b413-0e51724ef21c,d14df3cf-e4ff-4ae1-ba68-e6edad46462c
 Host: localhost:8080
 Accept-Encoding: gzip, deflate
 Content-Length: 44
 Connection: keep-alive
 cache-control: no-cache
 
 {
   "order": {
     "4": 5,
     "5": 10
   }
 }
  ```
  
- Users can return one or more movies, even from different orders in just one api call using this endpoint 
 ```
PUT /api/rental-orders/return HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU5OTY0OTg5NX0.dIlrtTbc15T8-50nKtogTbHOKF7WKmHZtv_1InTUgL8Iudr5fD0P9TuB9JfAMlDu8MMygz83DqdZqUng-PNovw,Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTU5OTc4MTY5Nn0.Ht03bW0aapT9Tjh2n10XPZq7ogD_c83np9I0kiwgnYHAowat-7zmYzXcUREIBApaOlrIRaGUrQ8bsxLM0u555A
Content-Type: application/json
User-Agent: PostmanRuntime/7.16.3
Accept: */*
Cache-Control: no-cache
Postman-Token: 8d1c2727-5d14-4e37-9eb3-9ff5a5abe39c,9e18daea-76d3-49bd-bf89-86b1583ccfe0
Host: localhost:8080
Accept-Encoding: gzip, deflate
Content-Length: 32
Connection: keep-alive
cache-control: no-cache

{
  "movieInventoryIds": [4,5]
}

 ```
- The Video Rental Store has covered with tests most of the functionality  


- I forgot something for sure but if you have any questions or doubts please feel free to reach me on roundrobine@gmail.com :) 





## Development


### Requirements

    1. Java 11
    2. Docker
    3. Elasticsearch

    

### Elasticsearch

I have decided to use Elasticsearch to enable intelligent, fast and reliable a search for all business objects in the Video Rental Store.
So before running the application in dev or prod mode it is a requirement to start an elasticsearch container using docker-compose script.
Spring Data Elasticsearch is used, with the help of Spring Data Jest. Spring Data Jest which allows communication with Elasticsearch’s REST API. It disables Spring Boot’s autoconfiguration and uses its own instead.
All package has new subpackage, called “search”, that holds all Elasticsearch repositories.
All business entity gets indexed in Elasticsearch, and you can query is using the /api/_search/{entity-path}/:query REST endpoint.

The easiest way to run an external Elasticsearch instance is to use the provided Docker Compose configuration:

```
docker-compose -f src/main/docker/elasticsearch.yml up -d
```
You will need docker installed on your machine. 

To start your application in the dev profile, run:

```
 ./mvnw
 ```
 Alternatively, if you have installed Maven, you can launch the Java server with Maven:
 
 ```
 mvn
 ```
 

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

## Building for production

### Packaging as jar

To build the final jar and optimize the videoRental application for production, run:

```

./mvnw -Pprod clean verify


```

To ensure everything worked, run:

```

java -jar target/*.jar


```

## Testing

To launch your application's tests, run:

```
./mvnw clean verify
```

## Using Docker to simplify development

You can use Docker to improve your development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a mysql database in a docker container, run:

```
docker-compose -f src/main/docker/mysql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/mysql.yml down
```

You can also fully dockerize the application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```

