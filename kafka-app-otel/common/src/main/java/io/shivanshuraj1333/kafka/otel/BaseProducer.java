package io.shivanshuraj1333.kafka.otel;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;

public abstract class BaseProducer {

    private static final String BOOTSTRAP_SERVERS_ENV_VAR = "BOOTSTRAP_SERVERS";
    private static final String TOPIC_ENV_VAR = "TOPIC";
    private static final String NUM_MESSAGES_ENV_VAR = "NUM_MESSAGES";
    private static final String DELAY_ENV_VAR = "DELAY";
    private static final String PARTITION_KEY_ENV_VAR = "PARTITION_KEY";
    private static final String PARTITION_KEY = "my-key";
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "my-topic";
    private static final String DEFAULT_NUM_MESSAGES = "1";
    private static final String DEFAULT_DELAY = "100";

    private static final Logger log = LogManager.getLogger(BaseProducer.class);

    protected String bootstrapServers;
    protected String topic;
    protected String producerKey;
    protected int numMessages;
    protected long delay;
    protected Producer<String, String> producer;

    public void run() {
        try {
            createKafkaProducer(loadKafkaProducerProperties());

            int messageCount = 0;
            while (messageCount < numMessages) {
                String message = "my-value-" + messageCount++;
                ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, this.producerKey, message);

                try {
                    this.producer.send(record, (metadata, exception) -> {
                        if (exception == null) {
                            log.info("Message [{}] sent to topic [{}] partition [{}] with offset [{}]",
                                    message, metadata.topic(), metadata.partition(), metadata.offset());
                        } else {
                            log.error("Error sending message [{}] to topic [{}]", message, this.topic, exception);
                        }
                    });
                } catch (SerializationException | TimeoutException e) {
                    log.error("Error serializing or sending the record", e);
                } catch (ProducerFencedException e) {
                    log.error("Producer encountered an unrecoverable error", e);
                    break;
                }

                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            log.error("Producer was interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
        } finally {
            if (this.producer != null) {
                try {
                    this.producer.close();
                } catch (Exception e) {
                    log.error("Error closing the producer", e);
                }
            }
        }
    }

    public void loadConfiguration(Map<String, String> map) {
        try {
            this.bootstrapServers = map.getOrDefault(BOOTSTRAP_SERVERS_ENV_VAR, DEFAULT_BOOTSTRAP_SERVERS);
            this.topic = map.getOrDefault(TOPIC_ENV_VAR, DEFAULT_TOPIC);
            this.producerKey = map.getOrDefault(PARTITION_KEY_ENV_VAR, PARTITION_KEY);
            this.numMessages = Integer.parseInt(map.getOrDefault(NUM_MESSAGES_ENV_VAR, DEFAULT_NUM_MESSAGES));
            this.delay = Long.parseLong(map.getOrDefault(DELAY_ENV_VAR, DEFAULT_DELAY));
            log.info("Configuration loaded: bootstrapServers={}, topic={}, producerKey={}, numMessages={}, delay={}",
                    bootstrapServers, topic, producerKey, numMessages, delay);
        } catch (NumberFormatException e) {
            log.error("Invalid number format in configuration", e);
            throw new IllegalArgumentException("Configuration error: Invalid number format", e);
        } catch (Exception e) {
            log.error("Error loading configuration", e);
            throw new RuntimeException("Configuration error", e);
        }
    }

    public Properties loadKafkaProducerProperties() {
        Properties props = new Properties();
        try {
            props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
            props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            log.info("Kafka Producer properties loaded successfully.");
        } catch (Exception e) {
            log.error("Error loading Kafka producer properties", e);
            throw new RuntimeException("Error in Kafka Producer properties", e);
        }
        return props;
    }

    public void createKafkaProducer(Properties props) {
        try {
            this.producer = new KafkaProducer<>(props);
            log.info("Kafka Producer created successfully.");
        } catch (Exception e) {
            log.error("Error creating Kafka Producer", e);
            throw new RuntimeException("Failed to create Kafka Producer", e);
        }
    }

}
