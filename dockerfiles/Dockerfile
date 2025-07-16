FROM bellsoft/liberica-openjdk-debian:24 as builder

WORKDIR /app
ADD . /app/neurowatch
RUN cd neurowatch && ./mvnw -Pproduction clean package

FROM bellsoft/liberica-openjre-debian:24
WORKDIR /app
COPY --from=builder /app/neurowatch/target/neurowatch-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]