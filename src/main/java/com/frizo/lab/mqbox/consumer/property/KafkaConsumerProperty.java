package com.frizo.lab.mqbox.consumer.property;

import com.frizo.lab.mqbox.producer.property.KafkaProducerProperty;

import java.util.List;
import java.util.Properties;

public class KafkaConsumerProperty {

    private Properties props;

    private List<String> topics;

    private Long internalMillis;

    public Properties getProps(){
        return props;
    }

    public List<String> getTopics(){
        return topics;
    }

    public Long getInternalMillis() {
        return internalMillis;
    }

    private KafkaConsumerProperty(Properties props, List<String> topics, Long internalMillis){
        this.props = props;
        this.topics = topics;
        this.internalMillis = internalMillis;
    }

    public static class KafkaConsumerPropertyBuilder {

        private Properties props;

        private List<String> topics;

        private Long internalMillis;

        private KafkaConsumerPropertyBuilder() {
        }

        public static KafkaConsumerPropertyBuilder newBuilder(){
            KafkaConsumerPropertyBuilder builder = new KafkaConsumerPropertyBuilder();
            builder.props = new Properties();
            return builder;
        }

        public KafkaConsumerPropertyBuilder bootstrapServers(List<String> addresses){
            if (addresses != null){
                String hosts = String.join(",", addresses);
                props.put("bootstrap.servers", hosts);
            }
            return this;
        }

        public KafkaConsumerPropertyBuilder groupId(String id){
            if (id != null){
                props.put("group.id", id);
            }
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
            if (param != null){
                props.put("auto.offset.reset", param);
            }
            return this;
        }

        public KafkaConsumerPropertyBuilder keyDeserializer(String deserializer){
            if(deserializer != null){
                props.put("key.deserializer", deserializer);
            }
            return this;
        }

        public KafkaConsumerPropertyBuilder valueDeserializer(String deserializer){
            if (deserializer != null){
                props.put("value.deserializer", deserializer);
            }
            return this;
        }

        public KafkaConsumerPropertyBuilder topics(List<String> topics){
            if (topics != null){
                this.topics = topics;
            }
            return this;
        }

        public KafkaConsumerPropertyBuilder enableAutoOffset(boolean enable){
            String enableStr = enable ? "true" : "false";
            props.put("enable.auto.offset.store", enableStr);
            return this;
        }

        public KafkaConsumerPropertyBuilder maxPollRecords(int size){
            props.put("max.poll.records", String.valueOf(size));
            return this;
        }

        public KafkaConsumerPropertyBuilder internalMillis(long param){
            this.internalMillis = param;
            return this;
        }

        public KafkaConsumerProperty build(){
            return new KafkaConsumerProperty(props, topics, internalMillis);
        }

    }

}
