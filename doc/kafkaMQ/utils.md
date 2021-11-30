# kafkaMQ 套件

<br>

---

<br>

官方文件地址：

https://kafka.apache.org/documentation

<br>

先介紹套件使用到的 pom 依賴：

<br>

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka_2.13</artifactId>
    <version>${kafka.version}</version>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>${kafka.version}</version>
</dependency>

<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>${kafka.version}</version>
</dependency>
```

<br>
<br>

[__官方套件重點知識整理__](utils_knowledge.md)

<br>
<br>
<br>
<br>

## Producer & Consumer

<br>
<br>

### MyKafkaProducer 實現：

<br>

MyKafkaProducer 實現 Producer 介面：

<br>

```java
public class MyKafkaProducer implements Producer<KafkaProducerProperty> {
    ...
}
```

<br>

先來看一下建構式：

<br>

```java
public MyKafkaProducer(KafkaProducerProperty property){
    this.property = property;
    this.producer = new KafkaProducer<String, String>(property.getProps());
}
```

<br>
<br>

`send()` 方法：

<br>

```java
public void send(String msg, String key) {
    producer.send(new ProducerRecord<String, String>(property.getTopic(), key, msg));
}
```

<br>

`shutdown()` 方法：

<br>

```java
public void shutdown() {
    producer.close();
}
```

<br>
<br>

### MyKafkaConsumer 實現：

<br>

MyKafkaConsumer 實現 Consumer 介面：

<br>

```java
public class MyKafkaConsumer implements Consumer<KafkaConsumerProperty> {
    ...
}
```

<br>

```java
public MyKafkaConsumer(RecordReader reader, KafkaConsumerProperty property){
    this.recordReader = reader;
    this.property = property;
}
```

<br>

`startup()` 方法：

<br>

這裡有一個 `Long internalMillis = property.getInternalMillis()` 參數，可以控制 consumer 拉取資料的頻率。

<br>

```java
public void startup() {
    this.consumer = new KafkaConsumer<String, String>(property.getProps());
    this.consumer.subscribe(property.getTopics());

    Long internalMillis = property.getInternalMillis();

    new Thread(() -> {
        ConsumerRecords<String, String> msgList;
        while (flag) {
            msgList = consumer.poll(Duration.ofMillis(internalMillis));
            msgList.forEach(msg -> {
                recordReader.processRecord(msg.value());
            });
            if (property.getProps().getProperty("enable.auto.commit").equals("false")){
                consumer.commitAsync();
            }
        }
        System.out.println("consumer shutdown.");
    }).start();
}
```

<br>

`shutdown()` 方法：

<br>

```java
public void shutdown() {
    this.flag = false;
    this.consumer.commitAsync();
    this.consumer.unsubscribe();
    this.consumer.close();
}
```

<br>
<br>
<br>
<br>

### Property 類別：

<br>

`KafkaConsumerProperty`

<br>

`KafkaProducerProperty`

<br>


這個類別承載所有 Kafka 啟動所需參數，使用 Builder 設計模式設計。

