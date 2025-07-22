FROM bellsoft/liberica-runtime-container:jdk-21-crac-cds-musl

WORKDIR /app
COPY ./target/neurowatch-0.0.1-SNAPSHOT-spring-boot.jar app.jar
ENTRYPOINT ["java", "-XX:CRaCCheckpointTo=/app/checkpoint", "-jar", "/app/app.jar"]