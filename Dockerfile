FROM openjdk:17-jdk-oracle
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/BlogBackend-0.0.1.jar /app
CMD ["java", "-jar", "BlogBackend-0.0.1.jar"]