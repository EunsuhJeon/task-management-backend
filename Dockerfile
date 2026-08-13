# ===== build =====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx512m"

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties ./
RUN chmod +x gradlew \
	&& test -f gradle/wrapper/gradle-wrapper.jar

COPY src src
RUN ./gradlew bootJar -x test --no-daemon --stacktrace

# ===== run =====
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
