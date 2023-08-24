# ==== CONFIGURE =====

# use eclipse-temurin jdk as builder
FROM eclipse-temurin:17-jdk-jammy as builder

# set workdir
WORKDIR /opt/app

# copy .mvn, .mvnw and pom.xml into .
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# ==== BUILD =====

# download dependencies
RUN ./mvnw dependency:go-offline

# copy the source code
COPY ./src ./src

# build the jar
RUN ./mvnw clean install -DskipTests

# ==== RUN =======

FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/app/*.jar" ]