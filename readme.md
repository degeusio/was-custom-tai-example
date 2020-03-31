## About

This project illustrates how to use a custom Trust Association Interceptor (TAI) with a Websphere application server.
The server hosts a Java EE application with one endpoint, which is protected using a JSR-250 @RolesAllowed annotation.

The TAI is used to resolve the Principal and associated role. 

## Build
```sh 
mvn clean package && docker build -f docker/Dockerfile -t io.degeus/javaee-app .
```
this may take some time to finish.

## Run
```sh
docker run --rm -p 8080:8080 -p 9080:9080 --name javaee-app io.degeus/javaee-app 
```
then check to see: http://localhost:9080/javaee-app/resources/ping and you'll see a 401

use curl:
```
curl -H "x-my-customtoken: taiUser:Administrator http://localhost:9080/javaee-app/resources/ping 
```
and you should see the response.

## Background
In (legacy) code, you may come across the use of TAIs. This is a quick project using Docker to quickly see how various
components work together. That may be useful to quickly get up to speed if you have to work on legacy or quickly refresh
your memory again.
