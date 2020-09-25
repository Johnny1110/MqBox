package com.frizo.lab.mqbox.consumer.impl;

import com.frizo.lab.mqbox.consumer.Consumer;
import com.frizo.lab.mqbox.consumer.processor.RecordReader;
import com.frizo.lab.mqbox.consumer.property.KafkaConsumerProperty;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

public class MyKafkaConsumer implements Consumer<KafkaConsumerProperty> {

    private final RecordReader recordReader;

    private KafkaConsumerProperty property;

    private KafkaConsumer<String, String> consumer;

    private volatile boolean flag = true;

    public MyKafkaConsumer(RecordReader reader, KafkaConsumerProperty property){
        this.recordReader = reader;
        this.property = property;
    }

    @Override
    public void startup() {
        this.consumer = new KafkaConsumer<String, String>(property.getProps());
        this.consumer.subscribe(property.getTopics());

        new Thread(() -> {
            ConsumerRecords<String, String> msgList;
            while (flag) {
                msgList = consumer.poll(Duration.ofMillis(1000L));
                msgList.forEach(msg -> {
                    recordReader.processRecord(msg.value());
                });
                if (property.getProps().getProperty("enable.auto.commit").equals("false")){
                    consumer.commitAsync();
                }
            }
            System.out.println("consumer shutdown.");
        }).start();
    }

    @Override
    public void shutdown() {
        this.flag = false;
        this.consumer.commitAsync();
        this.consumer.unsubscribe();
        this.consumer.close();
    }

    @Override
    public void setProperty(KafkaConsumerProperty property) {
        this.property = property;
    }

    @Override
    public KafkaConsumerProperty getProperty() {
        return this.property;
    }
}
