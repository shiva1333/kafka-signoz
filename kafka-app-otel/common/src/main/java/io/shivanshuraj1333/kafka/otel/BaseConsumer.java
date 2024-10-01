package io.shivanshuraj1333.kafka.otel;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseConsumer {
    private static final String BOOTSTRAP_SERVERS_ENV_VAR = "BOOTSTRAP_SERVERS";
    private static final String CONSUMER_GROUP_ENV_VAR = "CONSUMER_GROUP";
    private static final String TOPIC_ENV_VAR = "TOPIC";
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "topic1";
    private static final String DEFAULT_CONSUMER_GROUP = "my-consumer-group";

    public static final Logger log = LogManager.getLogger(BaseConsumer.class);

    protected String bootstrapServers;
    protected String consumerGroup;
    protected String topic;
    protected Consumer<String, String> consumer;

    protected AtomicBoolean running = new AtomicBoolean(true);

    public void run(CountDownLatch latch) {
        log.info("Subscribing to topic [{}]", this.topic);
        this.consumer.subscribe(List.of(this.topic));

        try {
            log.info("Polling for records...");
            while (this.running.get()) {
                try {
                    ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(10000));

                    for (ConsumerRecord<String, String> record : records) {
                        log.info("Received message key = [{}], value = [{}], offset = [{}]", record.key(), record.value(), record.offset());

                        try {
                            this.consumer.commitSync();
                            log.info("Successfully committed offset for record key = [{}]", record.key());
                        } catch (CommitFailedException cfe) {
                            log.error("CommitFailedException while committing offset for record key = [{}].", record.key(), cfe);
                        }
                    }
                } catch (WakeupException we) {
                    if (running.get()) {
                        log.warn("Received WakeupException while polling, but consumer is not shutting down.", we);
                        throw we;
                    }
                    log.info("Consumer shutdown initiated, WakeupException caught and handled.");
                } catch (Exception e) {
                    log.error("Unexpected error during polling. Consumer will continue polling.", e);
                }
            }
        } catch (Exception e) {
            log.error("Error in Kafka consumer run method, shutting down consumer.", e);
        } finally {
            try {
                log.info("Closing Kafka consumer...");
                this.consumer.unsubscribe();
                this.consumer.close();
                log.info("Kafka consumer closed.");
            } catch (Exception e) {
                log.error("Error occurred while closing Kafka consumer.", e);
            } finally {
                latch.countDown();
                log.info("CountDownLatch decremented, consumer run method exiting.");
            }
        }
    }


    public void loadConfiguration(Map<String, String> map) {
        this.bootstrapServers = map.getOrDefault(BOOTSTRAP_SERVERS_ENV_VAR, DEFAULT_BOOTSTRAP_SERVERS);
        this.consumerGroup = map.getOrDefault(CONSUMER_GROUP_ENV_VAR, DEFAULT_CONSUMER_GROUP);
        this.topic = map.getOrDefault(TOPIC_ENV_VAR, DEFAULT_TOPIC);
    }

    public Properties loadKafkaConsumerProperties() {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, this.consumerGroup);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
        props.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");

        return props;
    }

    public void createKafkaConsumer(Properties props) {
        this.consumer = new KafkaConsumer<>(props);
    }
}
