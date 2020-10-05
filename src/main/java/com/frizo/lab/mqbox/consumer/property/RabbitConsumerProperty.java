package com.frizo.lab.mqbox.consumer.property;

import java.util.Properties;

public class RabbitConsumerProperty {

    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private RabbitConsumerProperty(Properties props){
        properties = props;
    }

    public static class RabbitConsumerPropertyBuilder{

        private Properties properties;

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

        public RabbitConsumerProperty build(){
            return new RabbitConsumerProperty(this.properties);
        }
    }
}
