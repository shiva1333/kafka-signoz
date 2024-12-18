package io.shivanshuraj1333.kafka.otel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class Consumer extends BaseConsumer {
    private static final Logger log = LogManager.getLogger(Consumer.class);

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        try {
            consumer.initialize();
            
            Thread consumerThread = new Thread(consumer, "Kafka-Consumer-Thread");
            consumerThread.start();
            log.info("Kafka Consumer started successfully.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down consumer...");
                consumer.shutdown();
                try {
                    consumerThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));

        } catch (Exception e) {
            log.error("Failed to initialize and start the Kafka Consumer", e);
            System.exit(1);
        }
    }

    private void initialize() {
        loadConfiguration(System.getenv());
        Properties props = loadKafkaConsumerProperties();
        createKafkaConsumer(props);
    }
}
