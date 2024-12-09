# Use an OpenJDK base image
FROM openjdk:22-jdk-slim

# Create a directory for the application
RUN mkdir -p /opt

# Copy the JAR files into the Docker image
COPY opentelemetry-javaagent.jar /opt/opentelemetry-javaagent.jar
COPY kafka-consumer.jar /opt/kafka-consumer.jar

# Set environment variables
ENV BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
ENV CONSUMER_GROUP="cg1"
ENV TOPIC="topic1"
ENV OTEL_SERVICE_NAME="consumer-svc"
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_METRICS_EXPORTER="otlp"
ENV OTEL_LOGS_EXPORTER="otlp"

# Define the entry point for the container
ENTRYPOINT ["java", "-javaagent:/opt/opentelemetry-javaagent.jar", \
            "-Dotel.instrumentation.kafka.producer-propagation.enabled=true", \
            "-Dotel.instrumentation.kafka.experimental-span-attributes=true", \
            "-Dotel.instrumentation.kafka.metric-reporter.enabled=true", \
            "-jar", "/opt/kafka-consumer.jar"]
