#!/bin/bash

# Set environment variables
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export TOPIC="topic1"
export PARTITION_KEY="key1"
export DELAY="100"
export OTEL_SERVICE_NAME="producer-svc"
export OTEL_TRACES_EXPORTER="otlp"
export OTEL_METRICS_EXPORTER="otlp"
export OTEL_LOGS_EXPORTER="otlp"


# Run the Java application
java -javaagent:/opt/opentelemetry-javaagent.jar -jar /opt/kafka-producer.jar

