package io.shivanshuraj1333.kafka.otel;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class BaseProducer implements Runnable {
    protected static final Logger log = LogManager.getLogger(BaseProducer.class);
    
    // Configuration Environment Variables
    private static final String BOOTSTRAP_SERVERS_ENV = "BOOTSTRAP_SERVERS";
    private static final String TOPIC_ENV = "TOPIC";
    private static final String PARTITION_KEY_ENV = "PARTITION_KEY";
    private static final String MESSAGES_PER_BATCH_ENV = "MESSAGES_PER_BATCH";
    private static final String WRITE_DELAY_MS_ENV = "WRITE_DELAY_MS";
    private static final String BATCH_DELAY_MS_ENV = "BATCH_DELAY_MS";
    
    // Default Values
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "topic1";
    private static final String DEFAULT_PARTITION_KEY = "default-key";
    private static final int DEFAULT_MESSAGES_PER_BATCH = 10;
    private static final long DEFAULT_WRITE_DELAY_MS = 1000;
    private static final long DEFAULT_BATCH_DELAY_MS = 5000;

    // Instance variables
    protected String bootstrapServers;
    protected String topic;
    protected String partitionKey;
    protected int messagesPerBatch;
    protected long writeDelayMs;
    protected long batchDelayMs;
    protected KafkaProducer<String, String> producer;
    protected final AtomicBoolean running = new AtomicBoolean(true);
    protected final AtomicLong messageCounter = new AtomicLong(0);

    protected void loadConfiguration(Map<String, String> env) {
        bootstrapServers = env.getOrDefault(BOOTSTRAP_SERVERS_ENV, DEFAULT_BOOTSTRAP_SERVERS);
        topic = env.getOrDefault(TOPIC_ENV, DEFAULT_TOPIC);
        partitionKey = env.getOrDefault(PARTITION_KEY_ENV, DEFAULT_PARTITION_KEY);
        messagesPerBatch = Integer.parseInt(env.getOrDefault(MESSAGES_PER_BATCH_ENV, 
                String.valueOf(DEFAULT_MESSAGES_PER_BATCH)));
        writeDelayMs = Long.parseLong(env.getOrDefault(WRITE_DELAY_MS_ENV, 
                String.valueOf(DEFAULT_WRITE_DELAY_MS)));
        batchDelayMs = Long.parseLong(env.getOrDefault(BATCH_DELAY_MS_ENV, 
                String.valueOf(DEFAULT_BATCH_DELAY_MS)));
        
        log.info("Configuration loaded - bootstrap: {}, topic: {}, partitionKey: {}", 
                bootstrapServers, topic, partitionKey);
        log.info("Batch configuration - messagesPerBatch: {}, writeDelay: {}ms, batchDelay: {}ms", 
                messagesPerBatch, writeDelayMs, batchDelayMs);
    }

    protected Properties loadKafkaProducerProperties() {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return props;
    }

    protected void createKafkaProducer(Properties props) {
        producer = new KafkaProducer<>(props);
        log.info("Kafka producer created successfully");
    }

    @Override
    public void run() {
        log.info("Starting producer message loop");
        while (running.get()) {
            try {
                processBatch();
                log.info("Batch completed. Waiting for {}ms before next batch", batchDelayMs);
                Thread.sleep(batchDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in producer loop", e);
            }
        }
    }

    protected void processBatch() throws InterruptedException {
        log.info("Starting new batch of {} messages", messagesPerBatch);
        String[] topics = topic.split(",");
        
        for (int i = 0; i < messagesPerBatch && running.get(); i++) {
            // Select topic in round-robin fashion
            String selectedTopic = topics[i % topics.length];
            String message = generateMessage();
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                selectedTopic,
                null,  // Let Kafka handle partitioning
                System.currentTimeMillis(),
                String.valueOf(i),  // Use message number as key for even distribution
                message
            );
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error sending message: {}", message, exception);
                } else {
                    log.info("Message sent: topic={}, partition={}, offset={}, message={}", 
                            metadata.topic(), metadata.partition(), metadata.offset(), message);
                }
            });
            
            Thread.sleep(writeDelayMs);
        }
    }

    protected String generateMessage() {
        return String.format("Message-%d", messageCounter.incrementAndGet());
    }

    public void shutdown() {
        log.info("Initiating producer shutdown");
        running.set(false);
        if (producer != null) {
            producer.close();
            log.info("Producer closed");
        }
    }
}
