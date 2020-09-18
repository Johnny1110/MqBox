package com.frizo.lab.mqbox.producer.impl;

import com.frizo.lab.mqbox.producer.Producer;
import com.frizo.lab.mqbox.producer.property.KafkaProducerProperty;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MyKafkaProducer implements Producer<KafkaProducerProperty> {

    private KafkaProducerProperty property;

    private KafkaProducer<String, String> producer;

    public MyKafkaProducer(KafkaProducerProperty property){
        this.property = property;
        this.producer = new KafkaProducer<String, String>(property.getProps());
    }

    @Override
    public void send(String msg) {
        producer.send(new ProducerRecord<String, String>(property.getTopic(), "Message", msg));
    }

    @Override
    public void shutdown() {
        producer.close();
    }

    @Override
    public void setProperty(KafkaProducerProperty property) {
        this.property = property;
        this.producer = new KafkaProducer<String, String>(property.getProps());
    }

    @Override
    public KafkaProducerProperty getProperty() {
        return this.property;
    }
}
