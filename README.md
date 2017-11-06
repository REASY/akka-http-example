# akka-http-example

# System Requirements

You will need:

- Installed [docker](https://www.docker.com) with at least 4 GBytes RAM,
- Installed [flyway](https://flywaydb.org/),
- Installed [SBT](http://www.scala-sbt.org/download.html).


# How to run application

1. You cloned repo and opened bash in that folder
2. Run all dockerized dependencies (database) via command `docker-compose up`. MSSQL is ready when you are able to see the message `=============== MSSQL SERVER SUCCESSFULLY STARTED ==========================`
3. Run `flyway -configFile=flyway.conf migrate` to create database schema
4. Run server using `sbt "server/runMain Boot"`
5. Open http://localhost:9000/search?q=YOUR_QUERY_HERE in your browser  to search on Google
6. Open http://localhost:9000/search in your browser to get all previous search queries
