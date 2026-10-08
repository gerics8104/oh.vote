FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/vote.jar vote.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "vote.jar"]