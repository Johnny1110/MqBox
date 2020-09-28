package com.frizo.lab.mqbox.minlab;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Producer {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;

        // Session: 一個傳送或接收訊息的執行緒
        Session session;
        // Destination :訊息的目的地;訊息傳送給誰.
        Destination destination;
        // MessageProducer:訊息傳送者
        MessageProducer producer;

        connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://127.0.0.1:61616");

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            destination = session.createQueue("test-queue");
            // 得到訊息生成者【傳送者】
            producer = session.createProducer(destination);
            // 設定不持久化
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            producer.send(session.createTextMessage("hello world!!!"));

            producer.close();
            session.close();
            connection.close();
            System.out.println("end");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


}
