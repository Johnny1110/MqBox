package com.frizo.lab.mqbox.consumer.impl;

import com.frizo.lab.mqbox.consumer.Consumer;
import com.frizo.lab.mqbox.consumer.processor.RecordReader;
import com.frizo.lab.mqbox.consumer.property.RabbitConsumerProperty;
import com.frizo.lab.mqbox.exception.RabbitProducerPropertyBuilderException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class MyRabbitConsumer implements Consumer<RabbitConsumerProperty> {

    private RabbitConsumerProperty property;

    private RecordReader<String> reader;

    private Channel channel;

    private Connection connection;

    private boolean autoAck;

    private String queueName;

    private String excName;

    private String excType;

    private boolean queueDurable;

    private boolean useDefaultEx;

    private List<String> routingKeys;

    public MyRabbitConsumer(RecordReader<String> reader, RabbitConsumerProperty property) {
        this.reader = reader;
        this.property = property;

        Properties properties = property.getProperties();

        ConnectionFactory factory = buildRabbitConnectionFactory(property);

        this.autoAck = (boolean) property.getProperties().get("autoAck");

        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        Optional<String> excName =  Optional.ofNullable(properties.getProperty("exchangeName"));
        Optional<String> queueName = Optional.ofNullable(properties.getProperty("queueName"));
        Optional<String> excType =  Optional.ofNullable(properties.getProperty("exchangeType"));
        Optional<Boolean> queuDurable = Optional.ofNullable((Boolean) properties.get("queueDurable"));
        Optional<List> routingKeys = Optional.ofNullable(property.getRoutingKeys());

        this.routingKeys = routingKeys.orElse(new ArrayList<String>());
        this.useDefaultEx = isUsingDefaultEx(properties);
        verifyDestination(useDefaultEx, excName, excType, queueName);

        if (useDefaultEx){
            this.queueName = queueName.get();
            this.queueDurable = queuDurable.orElse(false);
            try {
                channel.queueDeclare(this.queueName, this.queueDurable, false, false, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            try {
                this.excName = excName.get();
                this.excType = excType.get();
                if (this.queueName == null || this.queueName.equals("")){
                    this.queueName = channel.queueDeclare().getQueue();
                    System.out.println("queue name:" + this.queueName);
                }
                channel.exchangeDeclare(this.excName, this.excType);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.routingKeys.forEach(key -> {
                try {
                    channel.queueBind(this.queueName, this.excName, key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


    }


    @Override
    public void startup() {
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
    public RabbitConsumerProperty getProperty() {
        return null;
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

    private boolean isUsingDefaultEx(Properties properties) {
        Optional<String> exchangeTypeOpt = Optional.of(properties.getProperty("exchangeType"));
        String exchangeType = exchangeTypeOpt.orElse("default");
        return exchangeType.equals("default");
    }

    private void verifyDestination(boolean useDefaultEx, Optional<String> excName, Optional<String> excType, Optional<String> queueName) {
        if (useDefaultEx && excName.isPresent()){
            throw new RabbitProducerPropertyBuilderException("If using default Exchange, please delete exchangeName property.");
        }

        if (useDefaultEx && !queueName.isPresent()){
            throw new RabbitProducerPropertyBuilderException("If using default Exchange, you should give a queueName for property.");
        }

        if (!useDefaultEx && !excType.isPresent()){
            throw new RabbitProducerPropertyBuilderException("If not using default Exchange, you should give a certain exchange type for property.");
        }

        if (!useDefaultEx && !excName.isPresent()){
            throw new RabbitProducerPropertyBuilderException("if not using default Exchange, please give a certain exchange name for property");
        }
    }

}
