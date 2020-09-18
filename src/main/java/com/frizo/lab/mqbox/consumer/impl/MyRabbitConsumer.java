package com.frizo.lab.mqbox.consumer.impl;

import com.frizo.lab.mqbox.consumer.Consumer;
import com.frizo.lab.mqbox.consumer.processor.RecordReader;
import com.frizo.lab.mqbox.consumer.property.RabbitConsumerProperty;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MyRabbitConsumer implements Consumer<RabbitConsumerProperty> {

    private RabbitConsumerProperty property;

    private RecordReader<String> reader;

    private Channel channel;

    private Connection connection;

    public MyRabbitConsumer(RecordReader<String> reader, RabbitConsumerProperty property) {
        this.reader = reader;
        this.property = property;
    }

    @Override
    public void startup() {
        System.out.println("booting RabbitConsumer...");
        ConnectionFactory factory = buildRabbitConnectionFactory(property);
        String queueName = property.getProperties().getProperty("queueName");
        boolean autoAck = (boolean) property.getProperties().get("autoAck");
        System.out.println("queueName: " + queueName + " ack: " + autoAck);
        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            reader.processRecord(message);
            if (!autoAck) {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        try {
            channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void shutdown() {
        try {
            System.out.println("shutdown RabbitMQConsumer..");
            this.channel.close();
            this.connection.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void setProperty(RabbitConsumerProperty property) {
        this.property = property;
    }

    @Override
    public RabbitConsumerProperty getProperty() {
        return this.property;
    }

    private ConnectionFactory buildRabbitConnectionFactory(RabbitConsumerProperty property) {
        Properties props = property.getProperties();
        ConnectionFactory factory = new ConnectionFactory();
        Optional.ofNullable(props.getProperty("host")).ifPresent(factory::setHost);
        Optional.ofNullable(props.getProperty("virtualHost")).ifPresent(factory::setVirtualHost);
        Optional.ofNullable(props.getProperty("username")).ifPresent(factory::setUsername);
        Optional.ofNullable(props.getProperty("password")).ifPresent(factory::setPassword);
        Optional.ofNullable(props.get("port")).ifPresent(port -> {
            factory.setPort((Integer) port);
        });
        return factory;
    }

}
