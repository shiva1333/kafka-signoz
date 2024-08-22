java -javaagent:${PWD}/opentelemetry-javagent/opentelemetry-javaagent.jar \
       -Dotel.service.name=producer-svc \
       -Dotel.traces.exporter=otlp \
       -Dotel.metrics.exporter=otlp \
       -Dotel.logs.exporter=otlp \
       -jar ${PWD}/kafka-app-otel/kafka-producer/target/kafka-producer-1.0-SNAPSHOT-jar-with-dependencies.jar
