#!/bin/bash

# Set environment variables
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg1"
export TOPIC="topic1"

export OTEL_SERVICE_NAME="consumer-svc"
export OTEL_TRACES_EXPORTER="otlp"
export OTEL_METRICS_EXPORTER="otlp"
export OTEL_LOGS_EXPORTER="otlp"


# Run the Java application
java -javaagent:/opt/opentelemetry-javaagent.jar \
        -Dotel.instrumentation.kafka.producer-propagation.enabled=true \
        -Dotel.instrumentation.kafka.experimental-span-attributes=true \
        -Dotel.instrumentation.kafka.metric-reporter.enabled=true \
        -jar /opt/kafka-consumer.jar
