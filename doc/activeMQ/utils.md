# ActiveMQ 套件

<br>

---

<br>


官方文件地址：

https://activemq.apache.org/components/classic/documentation

<br>

先介紹套件使用到的 pom 依賴：

<br>

```xml
<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>activemq-all</artifactId>
    <version>5.12.0</version>
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

### 實現

<br>
<br>

### `MyActiveProducer`

<br>

`MyActiveProducer` 實現了 `Producer` 介面。

<br>

```java
public class MyActiveProducer implements Producer<ActiveProducerProperty>{
    ...
}
```

<br>
<br>

### 建構式：

<br>

```java
public MyActiveProducer(ActiveProducerProperty property){
    ...
}
```

<br>

內容過多，在下面做完整解析：

<br>

事前準備階段：

<br>

```java
this.property = property;
Properties props = property.getProperties();
String brokerUrl = props.getProperty("brokerUrl");

Optional.ofNullable(props.getProperty("username")).ifPresent(username -> {
     String password = props.getProperty("password");
    connectionFactory = new ActiveMQConnectionFactory(username, password, brokerUrl);
});

if (!Optional.ofNullable(props.getProperty("username")).isPresent()){
    connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
}
```

<br>

建立 connection 階段：

<br>

```java
connection = connectionFactory.createConnection();
connection.start();
session = connection.createSession((boolean)props.get("transactional"), (int)props.get("ackMode"));
```

<br>

建立 queue 或 topic 階段：

<br>

```java
Optional.ofNullable(props.getProperty("queueName")).ifPresent(queueName -> {
    try {
        destination = session.createQueue(queueName);
    } catch (Exception e) {
        e.printStackTrace();
        shutdown();
    }
});

Optional.ofNullable(props.getProperty("topicName")).ifPresent(topicName -> {
    try {
        destination = session.createQueue(topicName);
    } catch (Exception e) {
        e.printStackTrace();
        shutdown();
    }
});
```

<br>

建立 producer ：

<br>

```java
producer = session.createProducer(destination);

Optional.ofNullable(props.get("deliveryMode")).ifPresent(mode -> {
    try {
        producer.setDeliveryMode((Integer) mode);
    } catch (Exception e) {
        shutdown();
        e.printStackTrace();
    }
});
```

<br>

這邊的 deliveryMode 有兩種狀態，持久化 ( persistent ) 和非持久化 ( non-persistent )。根據 JMS 規範，默認的模式是持久的。使用 setDeliveryMode 在 MessageProducer 上為所有消息設置持久性標誌。也可以使用 send 方法在每個消息的基礎上指定它。持久性是單個消息的屬性。

主要區別在於，如果使用持久傳遞，消息將持久保存到磁盤/db 中，以便它們在代理重啟後仍然存在。使用非持久傳遞時，如果關閉一個代理，那麼將丟失所有傳輸中的消息。

這種差異的影響是持久性消息傳遞通常比非持久性傳遞慢，尤其是在不使用Async Sends 時。


<br>
<br>
<br>
<br>

### send( ) 方法

<br>

```java
@Override
public void send(String msg, String key) {
    try {
        producer.send(session.createTextMessage(msg));
    } catch (Exception e) {
        e.printStackTrace();
        shutdown();
    }
}
```

<br>
<br>
<br>
<br>

### shutdown( ) 方法

<br>

```java
@Override
public void shutdown() {
    try {
        producer.close();
        session.close();
        connection.close();
        System.out.println("ActiveProducer shutdown successfully.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

<br>
<br>
<br>
<br>

### `MyActiveComsumer`

<br>

`MyActiveComsumer` 實現了 `Consumer` 介面。

<br>

```java
public class MyActiveComsumer implements Consumer<ActiveConsumerProperty>{
    ...
}
```

<br>
<br>

### 建構式：

<br>

```java
public MyActiveConsumer(ActiveConsumerProperty property, RecordReader<String> recordReader){
    ...
}
```

<br>

建立 connectionFactory ：

<br>

```java
Optional.ofNullable(props.getProperty("username")).ifPresent(username -> {
    String password = props.getProperty("password");
    connectionFactory = new ActiveMQConnectionFactory(username, password, brokerUrl);
});

// 如果沒有 username 資訊，則使用默認 ActiveMQConnectionFactory。

if (!Optional.ofNullable(props.getProperty("username")).isPresent()){
    connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
}
```

<br>

建立 connection：

<br>

```java
connection = connectionFactory.createConnection();
connection.start();
```

<br>

建立 session：

<br>

```java
session = connection.createSession((boolean)props.get("transactional"), (int)props.get("ackMode"));
```

<br>

使用 session 建立 queue 或者 topic：

<br>

```java
Optional.ofNullable(props.getProperty("queueName")).ifPresent(queueName -> {
    try {
        destination = session.createQueue(queueName);
    } catch (Exception e) {
        e.printStackTrace();
        shutdown();
    }
});

Optional.ofNullable(props.getProperty("topicName")).ifPresent(topicName -> {
    try {
        destination = session.createQueue(topicName);
    } catch (Exception e) {
        e.printStackTrace();
        shutdown();
    }
});
```

<br>
<br>

### startup( ) 方法：

<br>


```java
@Override
public void startup() {
    try {
        consumer = session.createConsumer(destination);
        consumer.setMessageListener(message -> {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
            try {
                recordReader.processRecord(msg.getText());
                Optional.ofNullable(property.getProperties().get("ackMode")).ifPresent(ackMode -> {
                    if ((int)ackMode == 2){
                        try {
                            message.acknowledge();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
        shutdown();
    }
}
```

<br>

### shutdown( ) 方法：

<br>

```java
@Override
public void shutdown() {
    try {
        consumer.close();
        session.close();
        connection.close();
        System.out.println("ActiveConsumer shutdown successfully.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

<br>
<br>
<br>
<br>

## 使用方法：

<br>
<br>

### Producer 使用範例：

<br>
<br>

```java
ActiveProducerProperty property = ActiveProducerProperty.ActiveProducerPropertyBuilder.newBuilder()
        .ack(ActiveProducerProperty.AckMode.AUTO_ACKNOWLEDGE)
        .brokerUrl("tcp://127.0.0.1:61616")
        .username("admin")
        .password("admin")
        .transactional(false)
        .queueName("test_queue")
        .deliveryMode(ActiveProducerProperty.DeliveryMode.NON_PERSISTENT)
        .build();

Producer<ActiveProducerProperty> producer = new MyActiveProducer(property);

producer.send("This is msg-testing!", null);
producer.shutdown();
```

<br>
<br>

### Consumer 使用範例：

<br>
<br>

```java
ActiveConsumerProperty property = ActiveConsumerProperty.ActiveConsumerPropertyBuilder.newBuilder()
        .ack(ActiveConsumerProperty.AckMode.AUTO_ACKNOWLEDGE)
        .brokerUrl("tcp://127.0.0.1:61616")
        .username("admin")
        .password("admin")
        .transactional(false)
        .queueName("test_queue")
        .build();

Consumer<ActiveConsumerProperty> consumer = new MyActiveConsumer(property, new ActiveRecordReader());
consumer.startup();
```
