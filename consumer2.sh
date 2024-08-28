#!/bin/bash

# Set environment variables
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg2"
export TOPIC="topic2"

# Run the Java application
java -javaagent:${PWD}/opentelemetry-javagent/opentelemetry-javaagent.jar \
        -Dotel.service.name=consumer-svc \
        -Dotel.traces.exporter=otlp \
        -Dotel.metrics.exporter=otlp \
        -Dotel.logs.exporter=otlp \
        -Dotel.instrumentation.kafka.producer-propagation.enabled=true \
        -Dotel.instrumentation.kafka.experimental-span-attributes=true \
        -Dotel.instrumentation.kafka.metric-reporter.enabled=true \
        -jar ${PWD}/kafka-app-otel/kafka-consumer/target/kafka-consumer-1.0-SNAPSHOT-jar-with-dependencies.jar
