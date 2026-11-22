
# Build stage
FROM maven:3.9-eclipse-temurin-22 AS build

WORKDIR /app

COPY . .

# Configure an alternate Maven Central mirror to avoid HTTP 429 errors
RUN mkdir -p /root/.m2 && \
    printf '%s\n' \
    '<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>google-central</id>' \
    '      <mirrorOf>central</mirrorOf>' \
    '      <url>https://maven-central.storage-download.googleapis.com/maven2</url>' \
    '    </mirror>' \
    '  </mirrors>' \
    '</settings>' \
    > /root/.m2/settings.xml

# Build the Spring Boot application
RUN mvn -B -s /root/.m2/settings.xml clean package -DskipTests

# Run stage
FROM eclipse-temurin:22-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
