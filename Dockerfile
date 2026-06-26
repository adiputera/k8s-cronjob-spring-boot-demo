FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/k8s-cronjob-demo-0.0.1-SNAPSHOT.jar app.jar
# The entrypoint will be overridden by the Kubernetes CronJob to include the sleep delay
ENTRYPOINT ["java", "-jar", "app.jar"]
