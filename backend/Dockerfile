# ===============================
# Estágio 1: Build (Maven + Java 21)
# ===============================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

ENV LANG=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins


COPY src ./src

RUN mvn clean package -DskipTests

# ===============================
# Estágio 2: Runtime (Imagem Final Leve)
# ===============================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]