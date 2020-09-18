package com.frizo.lab.mqbox.demo;

import com.frizo.lab.mqbox.consumer.Consumer;
import com.frizo.lab.mqbox.consumer.impl.MyRabbitConsumer;
import com.frizo.lab.mqbox.consumer.property.RabbitConsumerProperty;

public class RabbitCustTest {

    public static void main(String[] args) throws InterruptedException {
        RabbitConsumerProperty property = RabbitConsumerProperty.RabbitConsumerPropertyBuilder.newBuilder()
                .hostName("localhost")
                .virtualHost("/")
                .autoAck(true)
                .queueName("test_queue")
                .build();

        Consumer<RabbitConsumerProperty> consumer = new MyRabbitConsumer(new RabbitRecordReader(), property);
        consumer.startup();

        System.out.println("start up 之後");

        Thread.sleep(3000);

        consumer.shutdown();
    }

}
