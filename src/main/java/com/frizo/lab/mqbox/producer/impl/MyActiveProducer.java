package com.frizo.lab.mqbox.producer.impl;

import com.frizo.lab.mqbox.producer.Producer;
import com.frizo.lab.mqbox.producer.property.ActiveProducerProperty;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Optional;
import java.util.Properties;

public class MyActiveProducer implements Producer<ActiveProducerProperty> {

    private ActiveProducerProperty property;

    private Connection connection;

    private Destination destination;

    private MessageProducer producer;

    private Session session;

    private ConnectionFactory connectionFactory;

    public MyActiveProducer(ActiveProducerProperty property){
        this.property = property;
        Properties props = property.getProperties();
        String brokerUrl = props.getProperty("brokerUrl");

        Optional.ofNullable(props.getProperty("username")).ifPresent(username -> {
            String password = props.getProperty("password");
            connectionFactory = new ActiveMQConnectionFactory(username, password, brokerUrl);
        });

        if (!Optional.ofNullable(props.getProperty("username")).isPresent()){
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        }

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession((boolean)props.get("transactional"), (int)props.get("ackMode"));

            Optional.ofNullable(props.getProperty("queueName")).ifPresent(queueName -> {
                try {
                    destination = session.createQueue(queueName);
                } catch (Exception e) {
                    e.printStackTrace();
                    shutdown();
                }
            });

            Optional.ofNullable(props.getProperty("topicName")).ifPresent(topicName -> {
                try {
                    destination = session.createQueue(topicName);
                } catch (Exception e) {
                    e.printStackTrace();
                    shutdown();
                }
            });

            producer = session.createProducer(destination);

            Optional.ofNullable(props.get("deliveryMode")).ifPresent(mode -> {
                try {
                    producer.setDeliveryMode((Integer) mode);
                } catch (Exception e) {
                    shutdown();
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }

    }

    @Override
    public void send(String msg) {
        try {
            producer.send(session.createTextMessage(msg));
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    @Override
    public void shutdown() {
        try {
            producer.close();
            session.close();
            connection.close();
            System.out.println("ActiveProducer shutdown successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ActiveProducerProperty getProperty() {
        return this.property;
    }
}
