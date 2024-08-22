## configure producer env variables

```bash
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export TOPIC="topic1"
export PARTITION_KEY="key1"
```

## configure consumer env variables

```bash
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg1"
export TOPIC="topic1"
```

## configure producer env variables

```bash
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export TOPIC="topic2"
export PARTITION_KEY="key2"
```

## configure consumer env variables

```bash
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg2"
export TOPIC="topic2"
```

## tagMap, stringTagMap, numberTagMap

```bash
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg3"
export TOPIC="topic1"
```

---
```bash
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg4"
export TOPIC="topic2"
```

---
 # start consumer
```bash
java -javaagent:${PWD}/opentelemetry-javagent/opentelemetry-javaagent.jar \
        -Dotel.service.name=consumer-svc \
        -Dotel.traces.exporter=otlp \
        -Dotel.metrics.exporter=otlp \
        -Dotel.logs.exporter=otlp \
        -Dotel.instrumentation.kafka.producer-propagation.enabled=true \
        -Dotel.instrumentation.kafka.experimental-span-attributes=true \
        -Dotel.instrumentation.kafka.metric-reporter.enabled=true \
        -jar ${PWD}/kafka-app-otel/kafka-consumer/target/kafka-consumer-1.0-SNAPSHOT-jar-with-dependencies.jar
```
---
# start a producer
```bash
java -javaagent:${PWD}/opentelemetry-javagent/opentelemetry-javaagent.jar \
       -Dotel.service.name=producer-svc \
       -Dotel.traces.exporter=otlp \
       -Dotel.metrics.exporter=otlp \
       -Dotel.logs.exporter=otlp \
       -jar ${PWD}/kafka-app-otel/kafka-producer/target/kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar
```
---


