# NeuroWatch Demo App

Spring Boot application built with Maven designed for showcasing various solutions in/around Java ecosystem. Ongoing effort.

## Features So Far

- User authentication and authorization with Spring Security
- Persistence layer in MongoDB
- Smart assistant with Spring AI 
- Vaadin-based frontend
- Containerized deployment with Docker

## Prerequisites

- Java 25
- Docker and Docker Compose

## Running the Application Locally

1. Build and start the MongoDB and Ollama containers:
   ```bash
   docker-compose up -d mongodb ollama
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

## Testing the smart Assistant

The smart assistant implements the implant incident triage workflow. 
A user reports unusual telemetry in a place and time window, and the system turns that into a full incident case 
with risk level, affected devices, likely cause, and a detailed containment plan.

Test data already includes logs with anomalies so that the assistant has something to work with.
Yo test this functionality, go to /assistant and paste this input:

   ```text
   Investigate abnormal telemetry near lat 40.75217 lon -73.98759, radius 6000m, last 36 hours, metric neuralLatencyMs, threshold 20. Provide a containment plan.
   ```