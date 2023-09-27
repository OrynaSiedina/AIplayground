FROM gradle:8.1.1-jdk17 as builder

WORKDIR /app
COPY ./backend .
RUN gradle build --no-daemon
FROM openjdk:17

COPY .env .env

# Copy the JAR from builder stage
COPY --from=builder /app/build/libs/plAIgroundBackend-0.0.1-SNAPSHOT.jar /plAIgroundBackend-0.0.1-SNAPSHOT.jar
EXPOSE 8080
# Run the JAR file
ENTRYPOINT ["java","-jar","/plAIgroundBackend-0.0.1-SNAPSHOT.jar"]
