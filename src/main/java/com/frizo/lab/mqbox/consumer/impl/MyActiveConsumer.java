package com.frizo.lab.mqbox.consumer.impl;

import com.frizo.lab.mqbox.consumer.Consumer;
import com.frizo.lab.mqbox.consumer.processor.RecordReader;
import com.frizo.lab.mqbox.consumer.property.ActiveConsumerProperty;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.util.Optional;
import java.util.Properties;

public class MyActiveConsumer implements Consumer<ActiveConsumerProperty> {

    private ActiveConsumerProperty property;

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private Destination destination;

    private MessageConsumer consumer;

    private RecordReader<String> recordReader;

    public MyActiveConsumer(ActiveConsumerProperty property, RecordReader<String> recordReader){
        this.property = property;
        this.recordReader = recordReader;

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

        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    @Override
    public void startup() {
        try {
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> {
                ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
                try {
                    recordReader.processRecord(msg.getText());
                    Optional.ofNullable(property.getProperties().get("ackMode")).ifPresent(ackMode -> {
                        if ((int)ackMode == 2){
                            try {
                                message.acknowledge();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    @Override
    public void shutdown() {
        try {
            consumer.close();
            session.close();
            connection.close();
            System.out.println("ActiveConsumer shutdown successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ActiveConsumerProperty getProperty() {
        return this.property;
    }
}
