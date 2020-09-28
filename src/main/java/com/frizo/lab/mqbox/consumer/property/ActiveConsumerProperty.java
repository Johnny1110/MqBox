package com.frizo.lab.mqbox.consumer.property;

import com.frizo.lab.mqbox.exception.ActiveProducerPropertyBuilderException;
import com.frizo.lab.mqbox.producer.property.ActiveProducerProperty;

import java.util.Optional;
import java.util.Properties;

public class ActiveConsumerProperty {

    private Properties properties;

    private ActiveConsumerProperty(Properties properties){
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public interface DeliveryMode {
        int NON_PERSISTENT = 1;
        int PERSISTENT = 2;
    }

    public interface AckMode {
        int AUTO_ACKNOWLEDGE = 1;
        int CLIENT_ACKNOWLEDGE = 2;
        int DUPS_OK_ACKNOWLEDGE = 3;
        int SESSION_TRANSACTED = 0;
    }

    public static class ActiveConsumerPropertyBuilder {
        private Properties properties;

        private ActiveConsumerPropertyBuilder(){ }

        public static ActiveConsumerPropertyBuilder newBuilder(){
            ActiveConsumerPropertyBuilder builder = new ActiveConsumerPropertyBuilder();
            builder.properties = new Properties();
            return builder;
        }

        public ActiveConsumerPropertyBuilder username(String username){
            properties.setProperty("username", username);
            return this;
        }

        public ActiveConsumerPropertyBuilder password(String password){
            properties.setProperty("password", password);
            return this;
        }

        public ActiveConsumerPropertyBuilder brokerUrl(String brokerUrl){
            properties.setProperty("brokerUrl", brokerUrl);
            return this;
        }

        public ActiveConsumerPropertyBuilder queueName(String queueName){
            Optional.ofNullable(this.properties.getProperty("topicName")).ifPresent((topicName) -> {
                throw new ActiveProducerPropertyBuilderException("you already set topic \"" + topicName + "\", and then you can't set queueName property.");
            });
            this.properties.setProperty("queueName", queueName);
            return this;
        }

        public ActiveConsumerPropertyBuilder topicName(String topicName){
            Optional.ofNullable(this.properties.getProperty("queueName")).ifPresent((queueName) -> {
                throw new ActiveProducerPropertyBuilderException("you already set queueName \"" + queueName + "\", and then you can't set topicName property.");
            });
            this.properties.setProperty("topicName", topicName);
            return this;
        }

        public ActiveConsumerPropertyBuilder ack(int ackMode){
            this.properties.put("ackMode", ackMode);
            return this;
        }

        public ActiveConsumerPropertyBuilder transactional(boolean transactional){
            this.properties.put("transactional", transactional);
            return this;
        }

        public ActiveConsumerProperty build(){
            return new ActiveConsumerProperty(this.properties);
        }
    }
}
