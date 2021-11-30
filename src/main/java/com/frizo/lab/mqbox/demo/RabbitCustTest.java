package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.consumer.Consumer;
import com.netpro.trinity.streamjob.mqbox.consumer.impl.MyRabbitConsumer;
import com.netpro.trinity.streamjob.mqbox.consumer.property.RabbitConsumerProperty;
import com.netpro.trinity.streamjob.mqbox.producer.property.RabbitExchangeTypes;

import java.util.ArrayList;
import java.util.List;

public class RabbitCustTest {

    public static void main(String[] args) throws InterruptedException {

        List<String> routingKeys = new ArrayList<>();
        routingKeys.add("abc");
        routingKeys.add("bbc");

        RabbitConsumerProperty property = RabbitConsumerProperty.RabbitConsumerPropertyBuilder.newBuilder()
                .hostName("localhost")
                .virtualHost("/")
                .exchangeName("myDirect")
                .exchangeType(RabbitExchangeTypes.DIRECT)
                .routingKeys(routingKeys)
                //.port(8181)
                .autoAck(true)
                .build();

        Consumer<RabbitConsumerProperty> consumer = new MyRabbitConsumer(new RabbitRecordReader(), property);
        consumer.startup();

        System.out.println("start up 之後");
    }

}
