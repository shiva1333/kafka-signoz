package io.shivanshuraj1333.kafka.otel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;

public class Producer extends BaseProducer {
    private static final Logger log = LogManager.getLogger(Producer.class);

    public static void main(String[] args) {
        Producer producer = new Producer();
        try {
            producer.initialize();
            
            Thread producerThread = new Thread(producer, "Kafka-Producer-Thread");
            producerThread.start();
            log.info("Kafka Producer started successfully.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down producer...");
                producer.shutdown();
                try {
                    producerThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));

        } catch (Exception e) {
            log.error("Failed to initialize and start the Kafka Producer", e);
            System.exit(1);
        }
    }

    private void initialize() {
        loadConfiguration(System.getenv());
        Properties props = loadKafkaProducerProperties();
        createKafkaProducer(props);
    }
}
