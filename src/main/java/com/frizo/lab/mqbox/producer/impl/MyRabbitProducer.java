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

    private boolean useDefaultEx = true;

    private String exchangeName;

    private String exchangeType;

    private String queueName;

    private boolean queueDurable;

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

        // 加入在準備階段宣告 queue  或  exchange 的邏輯。
        Optional<String> excName =  Optional.ofNullable(properties.getProperty("exchangeName"));
        Optional<String> queueName = Optional.ofNullable(properties.getProperty("queueName"));
        Optional<String> excType =  Optional.ofNullable(properties.getProperty("exchangeType"));
        Optional<Boolean> queuDurable = Optional.ofNullable((Boolean) properties.get("queueDurable"));
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
            this.exchangeName = excName.get();
            this.exchangeType = excType.get();
            try {
                channel.exchangeDeclare(exchangeName, exchangeType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void send(String msg, String routingKey) {
        try {
            if (useDefaultEx){
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"));
            }else{
                channel.basicPublish(exchangeName, routingKey, null, msg.getBytes("UTF-8"));
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
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
    public RabbitProducerProperty getProperty() {
        return this.property;
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
