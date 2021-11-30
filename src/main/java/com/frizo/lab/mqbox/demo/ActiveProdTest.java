package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.producer.Producer;
import com.netpro.trinity.streamjob.mqbox.producer.impl.MyActiveProducer;
import com.netpro.trinity.streamjob.mqbox.producer.property.ActiveProducerProperty;

public class ActiveProdTest {

    public static void main(String[] args) {
        ActiveProducerProperty property = ActiveProducerProperty.ActiveProducerPropertyBuilder.newBuilder()
                .ack(ActiveProducerProperty.AckMode.AUTO_ACKNOWLEDGE)
                .brokerUrl("tcp://127.0.0.1:61616")
                .username("admin")
                .password("admin")
                .transactional(false)
                .queueName("test_queue")
                .deliveryMode(ActiveProducerProperty.DeliveryMode.NON_PERSISTENT)
                .build();

        Producer<ActiveProducerProperty> producer = new MyActiveProducer(property);

        producer.send("This is msg-testing!", null);
        producer.shutdown();
    }

}
