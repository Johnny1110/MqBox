package com.frizo.lab.mqbox.producer.property;

import java.util.List;
import java.util.Properties;

public class KafkaProducerProperty {

    private Properties props;

    private String topic;

    public Properties getProps(){
        return props;
    }

    public String getTopic(){
        return topic;
    }

    private KafkaProducerProperty(Properties props, String topic){
        this.props = props;
        this.topic = topic;
    }

    public static class KafkaProducerPropertyBuilder {

        private Properties props;

        private String topic;

        private KafkaProducerPropertyBuilder() {
        }

        public static KafkaProducerPropertyBuilder newBuilder(){
            KafkaProducerPropertyBuilder builder = new KafkaProducerPropertyBuilder();
            builder.props = new Properties();
            return builder;
        }

        public KafkaProducerPropertyBuilder bootstrapServers(List<String> addresses){
            String hosts = String.join(",", addresses);
            props.put("bootstrap.servers", hosts);
            return this;
        }

        public KafkaProducerPropertyBuilder acks(String param){
            props.put("acks", param);
            return this;
        }

        public KafkaProducerPropertyBuilder retries(int num){
            props.put("retries", num);
            return this;
        }

        public KafkaProducerPropertyBuilder batchSize(int batchSize){
            props.put("batch.size", batchSize);
            return this;
        }

        public KafkaProducerPropertyBuilder keySerializer(String seserializer){
            props.put("key.serializer", seserializer);
            return this;
        }

        public KafkaProducerPropertyBuilder valueSerializer(String seserializer){
            props.put("value.serializer", seserializer);
            return this;
        }

        public KafkaProducerPropertyBuilder topic(String topic){
            this.topic = topic;
            return this;
        }

        public KafkaProducerProperty build(){
            return new KafkaProducerProperty(props, topic);
        }

    }
}
