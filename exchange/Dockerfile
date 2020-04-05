FROM openjdk:8

COPY "build/libs/exchange-1.0-SNAPSHOT.jar" "app/exchange.jar"
WORKDIR app
EXPOSE 8080

CMD ["java", "-jar", "exchange.jar"]