## Stage 1: build
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

# copy sources
COPY . ./

# build the application
RUN mvn -B -DskipTests package


## Stage 2: runtime
FROM eclipse-temurin:17-jdk-alpine

# create non-root user
RUN addgroup -S appgroup && adduser -S app -G appgroup

WORKDIR /app

# copy jar from builder
COPY --from=builder /workspace/target/*.jar app.jar

# set ownership and switch to non-root
# the group created above is `appgroup`, so use that in chown
RUN chown -R app:appgroup /app
USER app

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
