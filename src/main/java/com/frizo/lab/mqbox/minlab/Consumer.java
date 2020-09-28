package com.frizo.lab.mqbox.minlab;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class Consumer {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        // Connection :JMS 客戶端到JMS Provider 的連線
        Connection connection = null;
        // Session: 一個傳送或接收訊息的執行緒
        Session session;
        // Destination :訊息的目的地;訊息傳送給誰.
        Destination destination;
        // 消費者,訊息接收者
        MessageConsumer consumer;
        connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");


        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);

            destination = session.createQueue("test-queue");

            consumer = session.createConsumer(destination);

            consumer.setMessageListener(message -> {
                ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
                System.out.println(message);
                try {
                    System.out.println(msg.getText());
                    message.acknowledge();
                } catch (JMSException e) {
                    e.printStackTrace();
                }

            });


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
