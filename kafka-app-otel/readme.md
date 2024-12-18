# Kafka Application with OpenTelemetry

A Kafka application demonstrating producer-consumer pattern with OpenTelemetry auto-instrumentation. Features multiple producers and consumers creating wave-like consumer lag patterns.

## Project Structure
```
├── common/                     # Shared components
│   ├── BaseProducer.java      # Base producer implementation
│   ├── BaseConsumer.java      # Base consumer implementation
│   └── pom.xml                # Common dependencies
├── docker/                    # Docker compose files
├── k8s-charts/               # Kubernetes manifests
├── kafka-producer/           # Producer module
└── kafka-consumer/           # Consumer module
```

## Quick Start

1. **Build Application**
```bash
mvn clean package
```

2. **Build & Push Docker Images**
```bash
# Build images for both architectures
make build-all

# Push to Docker Hub (requires docker login)
make push-all
```

3. **Run Application**

Using Docker Compose:
```bash
cd docker
# For AMD64
ARCH=amd64 docker-compose up -d
# For ARM64
ARCH=arm64 docker-compose up -d
```

Using Kubernetes:
```bash
# Apply in order
kubectl apply -f k8s-charts/kafka-infra.yaml
kubectl wait --for=condition=ready pod -l app=kafka-broker -n kafka-system --timeout=300s

kubectl apply -f k8s-charts/otel-collector.yaml
kubectl wait --for=condition=ready pod -l app=otel-collector -n kafka-system --timeout=120s

kubectl apply -f k8s-charts/kafka-producer.yaml
kubectl apply -f k8s-charts/kafka-consumer.yaml
```

## Configuration

### OpenTelemetry Settings
```properties
OTEL_SERVICE_NAME: kafka-producer/kafka-consumer
OTEL_TRACES_EXPORTER: otlp
OTEL_LOGS_EXPORTER: otlp
OTEL_EXPORTER_OTLP_ENDPOINT: http://otel-collector:4317
OTEL_EXPORTER_OTLP_PROTOCOL: grpc
OTEL_INSTRUMENTATION_KAFKA_ENABLED: true
OTEL_INSTRUMENTATION_KAFKA_CONTEXT_PROPAGATION_ENABLED: true
OTEL_PROPAGATORS: tracecontext,baggage
```

### Producer Settings
```properties
BOOTSTRAP_SERVERS: broker1:19092,broker2:19093,broker3:19094
TOPIC: topic1/topic2/topic3
MESSAGES_PER_BATCH: 1000-5000    # Varies by producer
WRITE_DELAY_MS: 50-200          # Varies by producer
BATCH_DELAY_MS: 1000-3000       # Varies by producer
```

### Consumer Settings
```properties
BOOTSTRAP_SERVERS: broker1:19092,broker2:19093,broker3:19094
CONSUMER_GROUP: wave-group-1/2/3
TOPIC: topic1/topic2/topic3
WAIT_BEFORE_NEXT_POLL_MS: 10-60    # Varies by consumer
MESSAGE_PROCESSING_TIME_MS: 100-350 # Varies by consumer
```

## Monitoring

View application telemetry:
```bash
# Producer logs with trace context
docker-compose logs -f kafka-producer-1
docker-compose logs -f kafka-producer-2
docker-compose logs -f kafka-producer-3

# Consumer logs with trace context
docker-compose logs -f kafka-consumer-1
docker-compose logs -f kafka-consumer-2
docker-compose logs -f kafka-consumer-3

# OpenTelemetry collector logs
docker-compose logs -f otel-collector
```

## Cleanup
```bash
# Docker Compose
docker-compose down

# Docker Images
make clean

# Kubernetes
kubectl delete -f k8s-charts/
```

## Kafka Internals
```bash
============================================================
Kafka Topic Distribution with 2 Brokers and 3 Partitions
Replication Factor = 2
============================================================

            Topic: sample_topic (3 partitions, RF=2)
            ---------------------------------------

                  +-----------------------+
                  |       Broker 1        |
                  +-----------------------+
                      | P1 Leader         | <--- Partition 1 (Leader)
                      | P2 Follower       | <--- Partition 2 (Replica)
                      | P3 Leader         | <--- Partition 3 (Leader)
                  +-----------------------+

                  +-----------------------+
                  |       Broker 2        |
                  +-----------------------+
                      | P1 Follower       | <--- Partition 1 (Replica)
                      | P2 Leader         | <--- Partition 2 (Leader)
                      | P3 Follower       | <--- Partition 3 (Replica)
                  +-----------------------+

============================================================
Explanation:
- Each topic is divided into partitions (P1, P2, P3).
- A partition can have a leader and one or more followers (based on RF).
- Leaders handle writes/reads, while followers replicate data for fault tolerance.
- Here:
    - Partition 1 (P1) leader resides on Broker 1, with a follower on Broker 2.
    - Partition 2 (P2) leader resides on Broker 2, with a follower on Broker 1.
    - Partition 3 (P3) leader resides on Broker 1, with a follower on Broker 2.
      ============================================================
```
