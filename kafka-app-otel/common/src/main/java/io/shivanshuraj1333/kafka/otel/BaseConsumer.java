package io.shivanshuraj1333.kafka.otel;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseConsumer implements Runnable {
    protected static final Logger log = LogManager.getLogger(BaseConsumer.class);
    
    // Configuration Environment Variables
    private static final String BOOTSTRAP_SERVERS_ENV = "BOOTSTRAP_SERVERS";
    private static final String CONSUMER_GROUP_ENV = "CONSUMER_GROUP";
    private static final String TOPIC_ENV = "TOPIC";
    private static final String WAIT_BEFORE_NEXT_POLL_MS_ENV = "WAIT_BEFORE_NEXT_POLL_MS";
    private static final String MESSAGE_PROCESSING_TIME_MS_ENV = "MESSAGE_PROCESSING_TIME_MS";
    
    // Default Values
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "topic1";
    private static final String DEFAULT_CONSUMER_GROUP = "minimal-consumer-group";
    private static final long DEFAULT_WAIT_BEFORE_NEXT_POLL_MS = 1000;
    private static final long DEFAULT_MESSAGE_PROCESSING_TIME_MS = 500;
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);

    // Instance variables
    protected String bootstrapServers;
    protected String consumerGroup;
    protected String topic;
    protected long waitBeforeNextPollMs;
    protected long messageProcessingTimeMs;
    protected KafkaConsumer<String, String> consumer;
    protected final AtomicBoolean running = new AtomicBoolean(true);

    protected void loadConfiguration(Map<String, String> env) {
        bootstrapServers = env.getOrDefault(BOOTSTRAP_SERVERS_ENV, DEFAULT_BOOTSTRAP_SERVERS);
        consumerGroup = env.getOrDefault(CONSUMER_GROUP_ENV, DEFAULT_CONSUMER_GROUP);
        topic = env.getOrDefault(TOPIC_ENV, DEFAULT_TOPIC);
        waitBeforeNextPollMs = Long.parseLong(env.getOrDefault(
            WAIT_BEFORE_NEXT_POLL_MS_ENV, String.valueOf(DEFAULT_WAIT_BEFORE_NEXT_POLL_MS)));
        messageProcessingTimeMs = Long.parseLong(env.getOrDefault(
            MESSAGE_PROCESSING_TIME_MS_ENV, String.valueOf(DEFAULT_MESSAGE_PROCESSING_TIME_MS)));
        
        log.info("Configuration loaded - bootstrap: {}, group: {}, topic: {}", 
                bootstrapServers, consumerGroup, topic);
        log.info("Timing configuration - waitBeforeNextPoll: {}ms, messageProcessingTime: {}ms", 
                waitBeforeNextPollMs, messageProcessingTimeMs);
    }

    protected Properties loadKafkaConsumerProperties() {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
        return props;
    }

    protected void createKafkaConsumer(Properties props) {
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        log.info("Subscribed to topic: {}", topic);
    }

    @Override
    public void run() {
        log.info("Starting consumer message loop");
        while (running.get()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(POLL_TIMEOUT);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, String> record : records) {
                        processRecord(record);
                        simulateProcessing();
                    }
                    consumer.commitSync();
                    log.info("Committed offsets for {} records", records.count());
                }
                waitBeforeNextPoll();
            } catch (WakeupException e) {
                if (running.get()) throw e;
            } catch (Exception e) {
                log.error("Error processing records", e);
            }
        }
    }

    protected void processRecord(ConsumerRecord<String, String> record) {
        log.info("Received: key = {}, value = {}, partition = {}, offset = {}",
                record.key(), record.value(), record.partition(), record.offset());
    }

    private void simulateProcessing() {
        try {
            Thread.sleep(messageProcessingTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitBeforeNextPoll() {
        try {
            Thread.sleep(waitBeforeNextPollMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        log.info("Initiating consumer shutdown");
        running.set(false);
        if (consumer != null) {
            consumer.wakeup();
            try {
                consumer.close();
                log.info("Consumer closed");
            } catch (Exception e) {
                log.error("Error closing consumer", e);
            }
        }
    }
}
