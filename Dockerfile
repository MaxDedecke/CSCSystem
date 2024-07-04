FROM eclipse-temurin:21-jre
COPY target/*.jar app.jar
COPY target/classes/application.properties /application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "-Dspring.config.location=/application.properties"]
