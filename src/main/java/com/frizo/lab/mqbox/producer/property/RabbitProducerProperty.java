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
            if (username != null){
                this.properties.setProperty("username", username);
            }
            return this;
        }

        public RabbitProducerPropertyBuilder password(String password){
            if (password != null){
                this.properties.setProperty("password", password);
            }
            return this;
        }

        public RabbitProducerPropertyBuilder port(int port){
            this.properties.put("port", port);
            return this;
        }

        public RabbitProducerPropertyBuilder hostName(String hostName){
            if (hostName != null){
                this.properties.put("hostName", hostName);
            }
            return this;
        }

        public RabbitProducerPropertyBuilder virtualHost(String virtualHost){
            if (virtualHost != null){
                this.properties.put("virtualHost", virtualHost);
            }
            return this;
        }

        public RabbitProducerPropertyBuilder exchangeName(String name){
            if (name != null){
                this.properties.setProperty("exchangeName", name);
            }
            return this;
        }

        public RabbitProducerPropertyBuilder exchangeType(RabbitExchangeTypes type){
            if (type != null){
                this.properties.setProperty("exchangeType", type.toString());
            }
            return this;
        }

        public RabbitProducerPropertyBuilder queueName(String queueName){
            if (queueName != null){
                this.properties.setProperty("queueName", queueName);
            }
            return this;
        }

        public RabbitProducerPropertyBuilder queueDurable(boolean queueDurable){
            String durable = String.valueOf(queueDurable);
            this.properties.setProperty("queueDurable", durable);
            return this;
        }

        public RabbitProducerProperty build(){
            return new RabbitProducerProperty(properties);
        }

    }

}