package com.frizo.lab.mqbox.producer.impl;

import com.frizo.lab.mqbox.exception.RabbitProducerPropertyBuilderException;
import com.frizo.lab.mqbox.producer.Producer;
import com.frizo.lab.mqbox.producer.property.RabbitProducerProperty;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MyRabbitProducer implements Producer<RabbitProducerProperty> {

    private RabbitProducerProperty property;

    private Channel channel;
    private Connection connection;

    public MyRabbitProducer(RabbitProducerProperty props){
        this.property = props;
        ConnectionFactory factory = new ConnectionFactory();
        Properties properties = property.getProperties();
        factory.setHost(properties.getProperty("hostName"));


        Optional.ofNullable(properties.getProperty("virtualHost")).ifPresent(factory::setVirtualHost);
        Optional.ofNullable(properties.getProperty("username")).ifPresent(factory::setUsername);
        Optional.ofNullable(properties.getProperty("password")).ifPresent(factory::setPassword);
        Optional.ofNullable(properties.get("port")).ifPresent(port -> {
            factory.setPort((Integer) port);
        });

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void send(String msg) {
        Properties properties = property.getProperties();
        Optional<String> excName =  Optional.ofNullable(properties.getProperty("exchangeName"));
        Optional<String> queueName = Optional.ofNullable(properties.getProperty("queueName"));
        verifyDestination(excName, queueName);

        // 直接透過默認 exchange (" ") 送往指定 queue。
        queueName.ifPresent(qName -> {
            try {
                channel.basicPublish("", qName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 把 msg 送往事先設定好的 exchange，並使用 routingKey 指定可接收 queue。
        excName.ifPresent(eName -> {
            String routingKey = properties.getProperty("routingKey");
            try {
                channel.basicPublish(eName, routingKey, null, msg.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void shutdown() {
        try {
            channel.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setProperty(RabbitProducerProperty property) {
        this.property = property;
    }

    @Override
    public RabbitProducerProperty getProperty() {
        return this.property;
    }

    private void verifyDestination(Optional<String> excName,  Optional<String> queueName) {
        if (excName.isPresent() && queueName.isPresent()){
            throw new RabbitProducerPropertyBuilderException("Set properties error: \"exchangeName\" and \"queueName\" can't be setted at the same time.");
        }
    }

}
