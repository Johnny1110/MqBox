package com.frizo.lab.mqbox.demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveCustLib {

    public static void main(String[] args) {
        new Thread(new HelloWorldConsumer()).start();
    }

    public static class HelloWorldConsumer implements Runnable, ExceptionListener {

        @Override
        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();
                connection.setExceptionListener(this);

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("TEST.FOO");
                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);
                // Wait for a message
                Message message = consumer.receive(1000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: " + text);
                } else {
                    System.out.println("Received: " + message);
                }

                consumer.close();
                session.close();
                connection.close();
            }catch (Exception ex){

            }
        }

        @Override
        public void onException(JMSException e) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }
}
