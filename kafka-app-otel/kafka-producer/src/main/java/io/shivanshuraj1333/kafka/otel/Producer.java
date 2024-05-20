package io.shivanshuraj1333.kafka.otel;

import java.util.Properties;

public class Producer extends BaseProducer {

    public static void main(String[] args) throws InterruptedException {
        Producer producer = new Producer();
        producer.loadConfiguration(System.getenv());
        Properties props = producer.loadKafkaProducerProperties();
        producer.createKafkaProducer(props);

        Thread producerThread = new Thread(() -> producer.run());
        producerThread.start();

        Thread.sleep(5000);
        producerThread.join();
    }
}
