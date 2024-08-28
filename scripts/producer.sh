#!/bin/bash

# Set environment variables
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export TOPIC="topic1"
export PARTITION_KEY="key1"
export DELAY="100"

# Run the Java application
java -javaagent:${PWD}/opentelemetry-javagent/opentelemetry-javaagent.jar \
     -Dotel.service.name=producer-svc \
     -Dotel.traces.exporter=otlp \
     -Dotel.metrics.exporter=otlp \
     -Dotel.logs.exporter=otlp \
     -jar ${PWD}/kafka-app-otel/kafka-producer/target/kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar

