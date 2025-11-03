# NeuroWatch Demo App

Spring Boot application built with Maven designed for showcasing various solutions in/around Java ecosystem. Ongoing effort.

## Features So Far

- User authentication and authorization with Spring Security
- Persistence layer in MongoDB
- Live logs ingest with Apache Kafka Binder
- Vaadin-based frontend
- Containerized deployment with Docker

## Prerequisites

- Java 25
- Docker and Docker Compose

## Running the Application Locally

1. Build and start the MongoDB container:
   ```bash
   docker-compose up -d mongodb
   ```

2. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Access the application at http://localhost:8080
There are two default users with different privileges you can use to log in: admin/adminpass, user/userpass

## Running the Application With Docker Compose

1. Build and start the containers:
   ```bash
   docker-compose up -d
   ```

## Accessing the Live Logs Functionality

The application supports real-time ingest of implant monitoring logs from Kafka and displays them in a Live Logs Vaadin view with server push.

What it means:

- Kafka ingest (Spring Cloud Stream): The app consumes messages from topic neurowatch.logs.raw.
- Mongo persistence: Every log is stored in implant_logs.
- Real-time UI (“Live Logs”): A dedicated Vaadin view (/live-logs) streams incoming logs to the browser using @Push.

The Log Generator resides in a separate repo: https://github.com/des-felins/log-generator

### How to use it: 

1. As the main app and the log generator are in separate compose.yml files, you must first create a shared network:

```bash
docker network create neurowatch_net
```

2. Start the MongoDB instance, Red Panda, and the NeuroWatch application first:

```bash
docker compose up -d
```

3. Clone and start the log generator.
4. The logs will be available at http://localhost:8080/live-logs