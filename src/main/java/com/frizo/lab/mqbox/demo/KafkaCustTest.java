package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.consumer.Consumer;
import com.netpro.trinity.streamjob.mqbox.consumer.impl.MyKafkaConsumer;
import com.netpro.trinity.streamjob.mqbox.consumer.property.KafkaConsumerProperty;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.List;

public class KafkaCustTest {
    public static void main(String[] args) {
        List<String> addresses = new ArrayList<>();
        addresses.add("localhost:9092");

        List<String> topics = new ArrayList<>();
        topics.add("jikeh");

        KafkaConsumerProperty property = KafkaConsumerProperty.KafkaConsumerPropertyBuilder.newBuilder()
                .topics(topics)
                .bootstrapServers(addresses)
                .groupId("groupC")
                .enableAutoCommit(true)
                .autoCommitIntervalMs(1000)
                .sessionTimeoutMs(30000)
                .autoOffsetReset("earliest")
                .keyDeserializer(StringDeserializer.class.getName())
                .valueDeserializer(StringDeserializer.class.getName())
                .build();

        Consumer<KafkaConsumerProperty> consumer = new MyKafkaConsumer(new com.netpro.trinity.streamjob.mqbox.demo.KafkaRecordReader(), property);
        consumer.startup();
    }
}
