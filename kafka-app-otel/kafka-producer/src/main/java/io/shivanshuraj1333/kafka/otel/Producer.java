package io.shivanshuraj1333.kafka.otel;

import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Producer extends BaseProducer {

    private static final Logger log = LogManager.getLogger(Producer.class);

    public static void main(String[] args) {
        Producer producer = new Producer();
        try {
            Map<String, String> configMap = loadEnvironmentVariables();
            producer.loadConfiguration(configMap);

            Properties props = producer.loadKafkaProducerProperties();
            producer.createKafkaProducer(props);

            Thread producerThread = new Thread(producer::run, "Kafka-Producer-Thread");
            producerThread.start();
            log.info("Kafka Producer started successfully.");

            addShutdownHook(producer, producerThread);

        } catch (Exception e) {
            log.error("Failed to initialize and start the Kafka Producer", e);
        }
    }

    private static Map<String, String> loadEnvironmentVariables() {
        return System.getenv();
    }

    private static void addShutdownHook(Producer producer, Thread producerThread) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown initiated. Attempting to stop Kafka Producer...");
            try {
                producerThread.interrupt();
                producerThread.join();
                log.info("Kafka Producer stopped successfully.");
            } catch (InterruptedException e) {
                log.warn("Shutdown interrupted while waiting for producer thread to stop.", e);
                Thread.currentThread().interrupt(); // Restore interrupt status
            } catch (Exception e) {
                log.error("Error during Kafka Producer shutdown", e);
            } finally {
                if (producer.producer != null) {
                    try {
                        producer.producer.close();
                    } catch (Exception e) {
                        log.error("Error closing the Kafka Producer", e);
                    }
                }
            }
        }, "Kafka-Producer-Shutdown-Hook"));
    }
}
