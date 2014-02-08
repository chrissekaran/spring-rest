spring-rest
===========

A sample Spring based application used to expose Rest based web services

Submitted by: Christopher Sekaran
Date: Sat Feb 8 2014

The application uses Spring and JPA with hibernate as a persistence provider.

Once run locally using mvn tomcat:run or by deploying the webapp as a war in tomcat,
it exposes 4 restful methods which can be tested along with Sample data given below.

1.  Adding a Term
    http://localhost:8080/terms-spring-rest/spring/terms/
    Http.POST
    {"termId": "202", "termText":"Effective Java- Josh "}

2.  Remove a Term
    http://localhost:8080/terms-spring-rest/spring/terms/{termId}
    Http.DELETE

3.  Update a Term
    http://localhost:8080/terms-spring-rest/spring/terms/{termId}
    Http.PUT
    {"termId": "202", "termText":"Effective Java- Josh "} -- where 202 may be an existing id

4.  Retrieve all Terms
    http://localhost:8080/terms-spring-rest/spring/terms/
    Http.GET

5.  Retrieve Term by Id
    http://localhost:8080/terms-spring-rest/spring/terms/{termId}
    Http.GET

The project uses maven to build and contains integration tests for the Controller all important paths.

It also uses Exception handling with the @ControllerAdvice from Spring 3.2 and an integration test.




