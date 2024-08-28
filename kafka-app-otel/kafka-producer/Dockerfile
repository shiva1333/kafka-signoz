FROM openjdk:11-jre-slim as builder

# Copy the OpenTelemetry Java agent from the parent directory
COPY opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the JAR file
COPY target/kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar /app/kafka-producer.jar

# Copy the OpenTelemetry Java agent from the builder stage
COPY --from=builder /opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Create a script to load environment variables and run the application
RUN echo '#!/bin/sh\n\
set -e\n\
\n\
# Source the environment file if provided\n\
if [ -n "$ENV_FILE" ] && [ -f "$ENV_FILE" ]; then\n\
  . "$ENV_FILE"\n\
fi\n\
\n\
java -javaagent:/app/opentelemetry-javaagent.jar \
     -Dotel.service.name=${OTEL_SERVICE_NAME} \
     -Dotel.exporter.otlp.endpoint=${OTEL_ENDPOINT} \
     -Dotel.traces.exporter=${OTEL_TRACES_EXPORTER} \
     -Dotel.metrics.exporter=${OTEL_METRICS_EXPORTER} \
     -Dotel.logs.exporter=${OTEL_LOGS_EXPORTER} \
     -jar /app/kafka-producer.jar\n' > /app/run.sh && chmod +x /app/run.sh

ENTRYPOINT ["/app/run.sh"]