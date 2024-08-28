# Use an OpenJDK base image
FROM openjdk:11-jre-slim

# Create a directory for the application
RUN mkdir -p /opt

# Copy the JAR files into the Docker image
COPY opentelemetry-javaagent.jar /opt/opentelemetry-javaagent.jar
COPY kafka-producer.jar /opt/kafka-producer.jar

# Set environment variables
ENV BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
ENV TOPIC="topic1"
ENV PARTITION_KEY="key1"
ENV DELAY="100"
ENV OTEL_SERVICE_NAME="producer-svc"
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_METRICS_EXPORTER="otlp"
ENV OTEL_LOGS_EXPORTER="otlp"

# Define the entry point for the container
ENTRYPOINT ["java", "-javaagent:/opt/opentelemetry-javaagent.jar", "-jar", "/opt/kafka-producer.jar"]
