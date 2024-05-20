package io.shivanshuraj1333.kafka.otel;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Consumer extends BaseConsumer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Consumer consumer = new Consumer();
        consumer.loadConfiguration(System.getenv());
        Properties props = consumer.loadKafkaConsumerProperties();
        consumer.createKafkaConsumer(props);

        CountDownLatch latch = new CountDownLatch(1);
        Thread consumerThread = new Thread(() -> consumer.run(latch));
        consumerThread.start();

        System.in.read();
        consumer.running.set(false);
        latch.await(10000, TimeUnit.MILLISECONDS);
    }
}

