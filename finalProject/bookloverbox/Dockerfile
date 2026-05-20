# Многоэтапная сборка для оптимизации размера образа
FROM maven:3.9-amazoncorretto-11 AS build
WORKDIR /app

# Копируем файлы Maven для кэширования зависимостей
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Загружаем зависимости (этот слой будет кэшироваться)
RUN ./mvnw dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Финальный образ
FROM amazoncorretto:11-alpine

WORKDIR /app

# Создаем пользователя для запуска приложения (безопасность)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем jar из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Настройки JVM для контейнера
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
