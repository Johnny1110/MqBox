package com.frizo.lab.mqbox.consumer.property;

import com.frizo.lab.mqbox.producer.property.RabbitExchangeTypes;

import java.util.List;
import java.util.Properties;

public class RabbitConsumerProperty {

    private Properties properties;

    private List<String> routingKeys;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<String> getRoutingKeys(){
        return this.routingKeys;
    }

    private RabbitConsumerProperty(Properties props, List<String> routingKeys){
        this.properties = props;
        this.routingKeys = routingKeys;
    }

    public static class RabbitConsumerPropertyBuilder{

        private Properties properties;

        private List<String> routingKeys;

        private RabbitConsumerPropertyBuilder(){
        }

        public static RabbitConsumerPropertyBuilder newBuilder(){
            RabbitConsumerPropertyBuilder builder = new RabbitConsumerPropertyBuilder();
            builder.properties = new Properties();
            return builder;
        }

        public RabbitConsumerPropertyBuilder hostName(String host){
            if (host != null){
                this.properties.put("host", host);
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder virtualHost(String virtualHost){
            if (virtualHost != null){
                this.properties.put("virtualHost", virtualHost);
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder username(String username){
            if (username != null){
                this.properties.put("username", username);
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder password(String password){
            if (password != null){
                this.properties.put("password", password);
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder port(int port){
            this.properties.put("port", port);
            return this;
        }

        public RabbitConsumerPropertyBuilder queueName(String queueName){
            if (queueName != null){
                this.properties.put("queueName", queueName);
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder queueDruable(boolean druable){
            this.properties.put("queueDruable", druable);
            return this;
        }

        public RabbitConsumerPropertyBuilder autoAck(boolean autoAck){
            this.properties.put("autoAck", autoAck);
            return this;
        }

        public RabbitConsumerPropertyBuilder exchangeName(String name){
            if (name != null){
                this.properties.setProperty("exchangeName", name);
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder exchangeType(RabbitExchangeTypes type){
            if (type != null){
                this.properties.setProperty("exchangeType", type.toString());
            }
            return this;
        }

        public RabbitConsumerPropertyBuilder routingKeys(List<String> routingKeys){
            this.routingKeys = routingKeys;
            return this;
        }

        public RabbitConsumerProperty build(){
            return new RabbitConsumerProperty(this.properties, this.routingKeys);
        }
    }
}
