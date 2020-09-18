package com.frizo.lab.mqbox.consumer.property;

import java.util.List;
import java.util.Properties;

public class KafkaConsumerProperty {

    private Properties props;

    private List<String> topics;

    public Properties getProps(){
        return props;
    }

    public List<String> getTopics(){
        return topics;
    }

    private KafkaConsumerProperty(Properties props, List<String> topics){
        this.props = props;
        this.topics = topics;
    }

    public static class KafkaConsumerPropertyBuilder {

        private Properties props;

        private List<String> topics;


        private KafkaConsumerPropertyBuilder() {
        }

        public static KafkaConsumerPropertyBuilder newBuilder(){
            KafkaConsumerPropertyBuilder builder = new KafkaConsumerPropertyBuilder();
            builder.props = new Properties();
            return builder;
        }

        public KafkaConsumerPropertyBuilder bootstrapServers(List<String> addresses){
            String hosts = String.join(",", addresses);
            props.put("bootstrap.servers", hosts);
            return this;
        }

        public KafkaConsumerPropertyBuilder groupId(String id){
            props.put("group.id", id);
            return this;
        }

        public KafkaConsumerPropertyBuilder enableAutoCommit(boolean enable){
            String enableStr = enable ? "true" : "false";
            props.put("enable.auto.commit", enableStr);
            return this;
        }

        public KafkaConsumerPropertyBuilder autoCommitIntervalMs(int ms){
            props.put("auto.commit.interval.ms", String.valueOf(ms));
            return this;
        }

        public KafkaConsumerPropertyBuilder sessionTimeoutMs(int ms){
            props.put("session.timeout.ms", String.valueOf(ms));
            return this;
        }

        public KafkaConsumerPropertyBuilder autoOffsetReset(String param){
            props.put("auto.offset.reset", param);
            return this;
        }

        public KafkaConsumerPropertyBuilder keyDeserializer(String deserializer){
            props.put("key.deserializer", deserializer);
            return this;
        }

        public KafkaConsumerPropertyBuilder valueDeserializer(String deserializer){
            props.put("value.deserializer", deserializer);
            return this;
        }

        public KafkaConsumerPropertyBuilder topics(List<String> topics){
            this.topics = topics;
            return this;
        }

        public KafkaConsumerProperty build(){
            return new KafkaConsumerProperty(props, topics);
        }

    }

}
