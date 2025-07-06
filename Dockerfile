# --- AŞAMA 1: Build (İnşa Etme) Aşaması ---
# HATALI İMAJ: maven:3.8.5-openjdk-21
# DOĞRU İMAJ: Docker Hub'da var olan, Java 21 ve Maven içeren popüler bir imaj kullanıyoruz.
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
# 'go-offline' Maven'in bağımlılıkları indirip hazır etmesini sağlar.
RUN mvn dependency:go-offline
COPY src ./src
# Projeyi derleyip paketliyoruz.
RUN mvn package -DskipTests


# --- AŞAMA 2: Run (Çalıştırma) Aşaması ---
# Bu imaj adı doğru ve Java 21 için yaygın olarak kullanılan küçük boyutlu bir imajdır.
FROM openjdk:21-jdk-slim

WORKDIR /app
# 1. Aşamada oluşturulan .jar dosyasını bu yeni, temiz imaja kopyalıyoruz.
COPY --from=build /app/target/*.jar app.jar

# Konteyner çalıştığında Spring Boot uygulamasını başlatıyoruz.
ENTRYPOINT ["java", "-jar", "app.jar"]