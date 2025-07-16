# NeuroWatch Demo App

Spring Boot application built with Maven designed for showcasing various solutions in/around Java ecosystem. Ongoing effort.

## Features So Far

- User authentication and authorization with Spring Security
- Persistence layer in MongoDB
- Vaadin-based frontend
- Containerized deployment with Docker

## Prerequisites

- Java 24
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