package io.shivanshuraj1333.kafka.otel;

import java.util.Properties;

public class Consumer extends BaseConsumer {

    public static void main(String[] args) {
        Consumer consumer = new Consumer();

        try {
            // Load configuration from environment variables
            consumer.loadConfiguration(System.getenv());

            // Load Kafka consumer properties
            Properties props = consumer.loadKafkaConsumerProperties();
            consumer.createKafkaConsumer(props);

            // Start the consumer thread
            Thread consumerThread = new Thread(() -> {
                try {
                    log.info("Starting Kafka consumer thread...");
                    consumer.run();  // No latch needed, directly run the consumer
                } catch (Exception e) {
                    log.error("Error occurred while running Kafka consumer: ", e);
                } finally {
                    log.info("Kafka consumer thread has exited.");
                }
            });

            consumerThread.start();

            // Keep the main thread alive indefinitely to prevent the application from exiting
            log.info("Application is running. Press Ctrl+C to exit.");

            // Use an infinite loop to keep the application alive
            while (true) {
                try {
                    Thread.sleep(1000); // Sleep to reduce CPU usage
                } catch (InterruptedException e) {
                    log.error("Main thread interrupted. Exiting application.", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (Exception e) {
            log.error("Unexpected error in main method: ", e);
        }
    }
}
