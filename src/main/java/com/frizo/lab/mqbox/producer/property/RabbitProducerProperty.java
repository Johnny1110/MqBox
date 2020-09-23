package com.frizo.lab.mqbox.producer.property;

import java.util.Properties;

public class RabbitProducerProperty {

    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    private RabbitProducerProperty(Properties properties){
        this.properties = properties;
    }

    public static class RabbitProducerPropertyBuilder {

        private Properties properties;

        private RabbitProducerPropertyBuilder(){}

        public static RabbitProducerPropertyBuilder newBuilder(){
            RabbitProducerPropertyBuilder builder = new RabbitProducerPropertyBuilder();
            builder.properties = new Properties();
            return builder;
        }

        public RabbitProducerPropertyBuilder username(String username){
            this.properties.setProperty("username", username);
            return this;
        }

        public RabbitProducerPropertyBuilder password(String password){
            this.properties.setProperty("password", password);
            return this;
        }

        public RabbitProducerPropertyBuilder port(int port){
            this.properties.put("port", port);
            return this;
        }

        public RabbitProducerPropertyBuilder hostName(String hostName){
            this.properties.put("hostName", hostName);
            return this;
        }

        public RabbitProducerPropertyBuilder virtualHost(String virtualHost){
            this.properties.put("virtualHost", virtualHost);
            return this;
        }

        public RabbitProducerPropertyBuilder exchangeName(String name){
            this.properties.setProperty("exchangeName", name);
            return this;
        }

        public RabbitProducerPropertyBuilder exchangeType(String type){
            this.properties.setProperty("exchangeType", type);
            return this;
        }

        public RabbitProducerPropertyBuilder routingKey(String routingKey){
            this.properties.setProperty("routingKey", routingKey);
            return this;
        }

        public RabbitProducerPropertyBuilder queueName(String queueName){
            this.properties.setProperty("queueName", queueName);
            return this;
        }

        public RabbitProducerProperty build(){
            return new RabbitProducerProperty(properties);
        }

    }
}
