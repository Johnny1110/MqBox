package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.producer.Producer;
import com.netpro.trinity.streamjob.mqbox.producer.impl.MyRabbitProducer;
import com.netpro.trinity.streamjob.mqbox.producer.property.RabbitExchangeTypes;
import com.netpro.trinity.streamjob.mqbox.producer.property.RabbitProducerProperty;

import java.lang.reflect.Field;

public class RabbitProdTest {

    public static void main(String[] args) {

        Field f;

        RabbitProducerProperty property = RabbitProducerProperty.RabbitProducerPropertyBuilder.newBuilder()
                .hostName("localhost")
                .virtualHost("/")
                .exchangeName("myDirect")
                .exchangeType(RabbitExchangeTypes.DIRECT)
                //.port(8181)
                .build();

        Producer<RabbitProducerProperty> producer = new MyRabbitProducer(property);

        producer.send("Hello, this is MyRabbitProducer first testing!!!", "abc");

        producer.shutdown();

    }

}
