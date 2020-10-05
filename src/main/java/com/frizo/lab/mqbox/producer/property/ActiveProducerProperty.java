package com.frizo.lab.mqbox.producer.property;

import com.frizo.lab.mqbox.exception.ActiveProducerPropertyBuilderException;

import java.util.Optional;
import java.util.Properties;

public class ActiveProducerProperty {

    private Properties properties;

    private ActiveProducerProperty(Properties properties){
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

    public static class ActiveProducerPropertyBuilder{

        private Properties properties;

        private ActiveProducerPropertyBuilder(){ }

        public static ActiveProducerPropertyBuilder newBuilder(){
            ActiveProducerPropertyBuilder builder = new ActiveProducerPropertyBuilder();
            builder.properties = new Properties();
            return builder;
        }

        public ActiveProducerPropertyBuilder username(String username){
            if (username != null){
                this.properties.put("username", username);
            }
            return this;
        }

        public ActiveProducerPropertyBuilder password(String password){
            if (password != null){
                this.properties.put("password", password);
            }
            return this;
        }

        public ActiveProducerPropertyBuilder brokerUrl(String brokerUrl){
            if (brokerUrl != null){
                this.properties.put("brokerUrl", brokerUrl);
            }
            return this;
        }

        public ActiveProducerPropertyBuilder queueName(String queueName){
            if (queueName != null){
                Optional.ofNullable(this.properties.getProperty("topicName")).ifPresent((topicName) -> {
                    throw new ActiveProducerPropertyBuilderException("you already set topic \"" + topicName + "\", and then you can't set queueName property.");
                });
                this.properties.setProperty("queueName", queueName);
            }
            return this;
        }

        public ActiveProducerPropertyBuilder topicName(String topicName){
            if (topicName != null){
                Optional.ofNullable(this.properties.getProperty("queueName")).ifPresent((queueName) -> {
                    throw new ActiveProducerPropertyBuilderException("you already set queueName \"" + queueName + "\", and then you can't set topicName property.");
                });
                this.properties.setProperty("topicName", topicName);
            }
            return this;
        }

        public ActiveProducerPropertyBuilder deliveryMode(int deliveryMode){
            this.properties.put("deliveryMode", deliveryMode);
            return this;
        }

        public ActiveProducerPropertyBuilder ack(int ackMode){
            this.properties.put("ackMode", ackMode);
            return this;
        }

        public ActiveProducerPropertyBuilder transactional(boolean transactional){
            this.properties.put("transactional", transactional);
            return this;
        }

        public ActiveProducerProperty build(){
            return new ActiveProducerProperty(this.properties);
        }

    }

}
