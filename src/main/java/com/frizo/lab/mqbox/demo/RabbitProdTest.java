package com.frizo.lab.mqbox.demo;

import com.frizo.lab.mqbox.producer.Producer;
import com.frizo.lab.mqbox.producer.impl.MyRabbitProducer;
import com.frizo.lab.mqbox.producer.property.RabbitProducerProperty;

public class RabbitProdTest {

    public static void main(String[] args) {
        RabbitProducerProperty property = RabbitProducerProperty.RabbitProducerPropertyBuilder.newBuilder()
                .hostName("localhost")
                .exchangeName("amq.topic")
                .exchangeType("topic")
                .routingKey("kern.aa")
                .build();

        Producer<RabbitProducerProperty> producer = new MyRabbitProducer(property);

        producer.send("Hello, this is MyRabbitProducer first testing!!!");

        producer.shutdown();

    }

}
