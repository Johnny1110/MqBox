package com.frizo.lab.mqbox.demo;

import com.frizo.lab.mqbox.consumer.Consumer;
import com.frizo.lab.mqbox.consumer.impl.MyActiveConsumer;
import com.frizo.lab.mqbox.consumer.property.ActiveConsumerProperty;
import com.frizo.lab.mqbox.producer.Producer;
import com.frizo.lab.mqbox.producer.impl.MyActiveProducer;
import com.frizo.lab.mqbox.producer.property.ActiveProducerProperty;

public class ActiveCustTest {

    public static void main(String[] args) {
        ActiveConsumerProperty property = ActiveConsumerProperty.ActiveConsumerPropertyBuilder.newBuilder()
                .ack(ActiveConsumerProperty.AckMode.AUTO_ACKNOWLEDGE)
                .brokerUrl("tcp://127.0.0.1:61616")
                .username("admin")
                .password("admin")
                .transactional(false)
                .queueName("test_queue")
                .build();

        Consumer<ActiveConsumerProperty> consumer = new MyActiveConsumer(property, new ActiveRecordReader());
        consumer.startup();
    }

}
