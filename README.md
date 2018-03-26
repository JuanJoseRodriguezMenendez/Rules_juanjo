# Rules-External_Actions
 This repository contains the rules microservice to launch external actions through perseo in the FIWOO platform 

## Getting Started

### Prerequisites

- [Apache Maven 3.3.9]
- [Java 1.8]
- [Perseo-fe]
- [Perseo-core]
- [Orion Context Broker]
- [MongoDB]
- [MySQL]

# Development

Install Apache Maven and Java 1.8 in your host.

MongoDB and MySQL server need to be accessible in local or in docker instances.

Install or instanciate Docker instances for the following FIWARE Services:

- [Orion Context Broker](https://fiware-orion.readthedocs.io/en/master/)
- [Perseo-Core](https://github.com/telefonicaid/perseo-core)
- [Perseo-fe](https://github.com/telefonicaid/perseo-fe)

## Deployment with Docker

In order to deploy this microservice using Docker, follow these steps:

1. Download the docker image from Docker Hub

	`docker pull fiwoo/rules-external_actions`

2. Run the image. You have to inlude Environment Variables with the directions of Perseo, Orion and MySQL Server. Take into account that the microservice is started on port 4444.

	`sudo docker run -p 4444:4444 --name perseo_rules -e "PERSEO_HOST=<perseo fe URL>" -e "PERSEO_PORT=<perseo fe URL>" -e "FIWARE_SERVICE=<Fiware Service>" -e "FIWARE_SERVICEPATH=<Fiware service Path>" -e "ORION_HOST=<Orion URL>" -e "ORION_PORT=<Orion Port>" -e "JDBC_URL_STRING=jdbc:mysql://<Database URL>:<Database Port>/<Database name>" -e "JDBC_USER=<User in Database>" -e "JDBC_PASSWORD=<Passowrd of User>" -e "EUREKA_VHOST=<Eureka Host>" -e "EUREKA_PORT=<eureka port>" fiwoo/rules-external_actions
