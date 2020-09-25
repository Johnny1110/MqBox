package com.frizo.lab.mqbox.demo;

import com.frizo.lab.mqbox.producer.Producer;
import com.frizo.lab.mqbox.producer.impl.MyKafkaProducer;
import com.frizo.lab.mqbox.producer.property.KafkaProducerProperty;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;

public class KafkaProdTest {

    public static void main(String[] args) {

        List<String> addresses = new ArrayList<>();
        addresses.add("localhost:9092");

        String topic = "jikeh";

        KafkaProducerProperty property = KafkaProducerProperty.KafkaProducerPropertyBuilder.newBuilder()
                .bootstrapServers(addresses)
                .topic(topic)
                .acks("all")
                .retries(0)
                .batchSize(100)
                .keySerializer(StringSerializer.class.getName())
                .valueSerializer(StringSerializer.class.getName())
                .build();

        Producer producer = new MyKafkaProducer(property);

        for (int i = 0; i < 2000; ++i){
            producer.send("hello world " + i);
        }

        producer.shutdown();
    }

}
