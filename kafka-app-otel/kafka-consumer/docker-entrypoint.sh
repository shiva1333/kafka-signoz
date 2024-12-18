#!/bin/bash
set -e

# Default values for OpenTelemetry configuration
: "${OTEL_SERVICE_NAME:=kafka-consumer}"
: "${OTEL_TRACES_EXPORTER:=otlp}"
: "${OTEL_METRICS_EXPORTER:=otlp}"
: "${OTEL_LOGS_EXPORTER:=otlp}"
: "${OTEL_EXPORTER_OTLP_ENDPOINT:=http://otel-collector:4317}"
: "${OTEL_EXPORTER_OTLP_PROTOCOL:=grpc}"
: "${OTEL_RESOURCE_ATTRIBUTES:=service.name=kafka-consumer,deployment.environment=dev}"

# JVM options
JVM_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=70"

# OpenTelemetry agent options
OTEL_OPTS="-javaagent:/app/opentelemetry-javaagent.jar \
    -Dotel.service.name=${OTEL_SERVICE_NAME} \
    -Dotel.traces.exporter=${OTEL_TRACES_EXPORTER} \
    -Dotel.metrics.exporter=${OTEL_METRICS_EXPORTER} \
    -Dotel.logs.exporter=${OTEL_LOGS_EXPORTER} \
    -Dotel.exporter.otlp.endpoint=${OTEL_EXPORTER_OTLP_ENDPOINT} \
    -Dotel.exporter.otlp.protocol=${OTEL_EXPORTER_OTLP_PROTOCOL} \
    -Dotel.resource.attributes=${OTEL_RESOURCE_ATTRIBUTES}"

# Start the application
exec java ${JVM_OPTS} ${OTEL_OPTS} -jar /app/kafka-consumer.jar 