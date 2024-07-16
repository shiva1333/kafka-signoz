## configure producer env variables

export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export TOPIC="topic1"
export NUM_MESSAGES=50
export PARTITION_KEY="key1"

## configure consumer env variables

export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg1"
export TOPIC="topic1"

## configure producer env variables

export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export TOPIC="topic2"
export NUM_MESSAGES=50
export PARTITION_KEY="key2"

## configure consumer env variables

export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg2"
export TOPIC="topic2"

## tagMap, stringTagMap, numberTagMap

export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg3"
export TOPIC="topic1"

---
export BOOTSTRAP_SERVERS="127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094"
export CONSUMER_GROUP="cg4"
export TOPIC="topic2"