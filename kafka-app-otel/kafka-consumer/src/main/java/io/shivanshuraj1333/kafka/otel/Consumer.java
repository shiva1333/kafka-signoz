package io.shivanshuraj1333.kafka.otel;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Consumer extends BaseConsumer {

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        CountDownLatch latch = new CountDownLatch(1);

        try {
            consumer.loadConfiguration(System.getenv());

            Properties props = consumer.loadKafkaConsumerProperties();
            consumer.createKafkaConsumer(props);

            Thread consumerThread = new Thread(() -> {
                try {
                    consumer.run(latch);
                } catch (Exception e) {
                    log.error("Error occurred while running Kafka consumer: ", e);
                }
            });
            consumerThread.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutdown hook triggered, stopping the consumer...");
                consumer.running.set(false);

                try {
                    if (!latch.await(10000, TimeUnit.MILLISECONDS)) {
                        log.warn("Consumer did not shut down gracefully within the timeout.");
                    } else {
                        log.info("Consumer shut down gracefully.");
                    }
                } catch (InterruptedException e) {
                    log.error("Interrupted while waiting for consumer to shut down.", e);
                    Thread.currentThread().interrupt();
                }
            }));

            log.info("Application is running. Press Ctrl+C to exit.");
            latch.await();
        } catch (Exception e) {
            log.error("Unexpected error in main method: ", e);
        } finally {
            try {
                latch.countDown();
            } catch (Exception e) {
                log.error("Error during final latch countdown: ", e);
            }
            log.info("Application has exited.");
        }
    }
}



