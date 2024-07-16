import javax.management.ObjectName

def kafkaServerMetricsWithTopic = otel.mbeans([
    "kafka.server:type=BrokerTopicMetrics,name=*,clientId=*,topic=*,partition=*"
])

kafkaServerMetricsWithTopic.each { mbean ->
    try {
        def objectName = new ObjectName(mbean.getName())
        def type = objectName.getKeyProperty("type")
        def name = objectName.getKeyProperty("name")
        def clientId = objectName.getKeyProperty("clientId")
        def topic = objectName.getKeyProperty("topic")
        def partition = objectName.getKeyProperty("partition")

        otel.instrument(mbean,
            "kafka_server_${type}_${name}",
            "Kafka server metric with topic and partition",
            "{value}",
            [
                "clientId": clientId,
                "topic": topic,
                "partition": partition
            ],
            "Value", otel.&doubleValueCallback
        )
    } catch (Exception e) {
        println "Error processing MBean: ${mbean.getName()} - ${e.message}"
    }
}