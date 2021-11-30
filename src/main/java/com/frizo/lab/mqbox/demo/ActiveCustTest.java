package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.consumer.Consumer;
import com.netpro.trinity.streamjob.mqbox.consumer.impl.MyActiveConsumer;
import com.netpro.trinity.streamjob.mqbox.consumer.property.ActiveConsumerProperty;

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

        Consumer<ActiveConsumerProperty> consumer = new MyActiveConsumer(property, new com.netpro.trinity.streamjob.mqbox.demo.ActiveRecordReader());
        consumer.startup();
    }

}
