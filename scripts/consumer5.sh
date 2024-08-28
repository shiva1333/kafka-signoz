#!/bin/bash

# Set environment variables
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg3"
export TOPIC="topic3"
export OTEL_SERVICE_NAME="consumer-sv3"
export OTEL_TRACES_EXPORTER="otlp"
export OTEL_METRICS_EXPORTER="otlp"
export OTEL_LOGS_EXPORTER="otlp"

# Run the Java application
java -javaagent:${PWD}/../opentelemetry-javagent/opentelemetry-javaagent.jar \
        -Dotel.instrumentation.kafka.producer-propagation.enabled=true \
        -Dotel.instrumentation.kafka.experimental-span-attributes=true \
        -Dotel.instrumentation.kafka.metric-reporter.enabled=true \
        -jar ${PWD}/../docker/consumer/kafka-consumer.jar
