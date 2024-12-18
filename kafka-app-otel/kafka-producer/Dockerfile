FROM openjdk:22-slim

WORKDIR /app

# Install curl so we can download the OpenTelemetry agent
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Set the desired version of the OpenTelemetry Java agent as an environment variable
ENV OTEL_VERSION=2.10.0

# Download the OpenTelemetry Java agent JAR from GitHub releases
RUN curl -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar" -o opentelemetry-javaagent.jar

# Copy the application JAR
COPY target/kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar /app/kafka-producer.jar

# Copy the entrypoint script
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# Add multi-stage build to reduce image size
# Add non-root user for security

ENTRYPOINT ["/app/docker-entrypoint.sh"]
