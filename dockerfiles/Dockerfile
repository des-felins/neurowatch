FROM bellsoft/liberica-native-image-kit-container:jdk-24-nik-24-musl as builder
RUN apk add --no-cache nodejs npm
WORKDIR /app
ADD . /app/neurowatch
RUN cd neurowatch && ./mvnw -Pproduction -Pnative native:compile

FROM bellsoft/alpaquita-linux-base:musl
WORKDIR /app
ENTRYPOINT ["/app/app"]
COPY --from=builder /app/neurowatch/target/native/neurowatch /app/app