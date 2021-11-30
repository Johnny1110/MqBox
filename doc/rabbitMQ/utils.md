# rabbitMQ 套件

<br>

---

<br>

關於 queue 的概念，需要額外的去了解其目的以及工作原理，這些都可以再 rabbitMQ 的官方網站上找到文件學習。這裡就不介紹 queue 的觀念了。

官方文件地址：https://www.rabbitmq.com/tutorials/tutorial-one-python.html

<br>

先介紹套件使用到的 pom 依賴：

<br>

```xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>${rabbitmq.version}</version>
</dependency>
```

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

Message Queue 最主要的就是 Producer 與 Consumer。介面已經先設計好了，所以 Rabbit 部分的 Producer 與 Consumer 需要實現這兩個介面。

<br>
<br>

### `MyRabbitProducer`

<br>

`MyRabbitProducer` 實現了 `Producer` 介面。

<br>

```java
public class MyRabbitProducer implements Producer<RabbitProducerProperty>{
    ...
}
```

<br>
<br>

看一下建構函式，這個部份會有一點長，許多準備工作都要在物件初始化階段完成。

<br>

```java
public MyRabbitProducer(RabbitProducerProperty props){
    ...
}
```

<br>

由於內容過多，所以下面會拆分來做解釋：

<br>

### part-1 建立連線物件:

<br>

```java
this.property = props;
ConnectionFactory factory = new ConnectionFactory();
Properties properties = property.getProperties();
factory.setHost(properties.getProperty("hostName"));

Optional.ofNullable(properties.getProperty("virtualHost")).ifPresent(factory::setVirtualHost);
Optional.ofNullable(properties.getProperty("username")).ifPresent(factory::setUsername);
Optional.ofNullable(properties.getProperty("password")).ifPresent(factory::setPassword);
Optional.ofNullable(properties.get("port")).ifPresent(port -> {
    factory.setPort((Integer) port);
});

try {
    connection = factory.newConnection();
    channel = connection.createChannel();
} catch (Exception e) {
    e.printStackTrace();
}
```

<br>
<br>

### part-2 宣告 queue 與 exchange 邏輯:

<br>

```java
Optional<String> excName =  Optional.ofNullable(properties.getProperty("exchangeName"));
Optional<String> queueName = Optional.ofNullable(properties.getProperty("queueName"));
Optional<String> excType =  Optional.ofNullable(properties.getProperty("exchangeType"));
Optional<Boolean> queuDurable = Optional.ofNullable((Boolean) properties.get("queueDurable"));

this.useDefaultEx = isUsingDefaultEx(properties); // 檢查是否使用預設 exchange(不指定 exchangeName)
verifyDestination(useDefaultEx, excName, excType, queueName); // 檢查參數配置，如果不符合邏輯會報錯

if (useDefaultEx){
    this.queueName = queueName.get();
    this.queueDurable = queuDurable.orElse(false);
    try {
        channel.queueDeclare(this.queueName, this.queueDurable, false, false, null);
    } catch (IOException e) {
        e.printStackTrace();
    }
} else{
    this.exchangeName = excName.get();
    this.exchangeType = excType.get();
    try {
        channel.exchangeDeclare(exchangeName, exchangeType);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

<br>
<br>

### 方法 `void send(String msg, String routingKey)` :

<br>

```java
public void send(String msg, String routingKey) {
    try {
        if (useDefaultEx){
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"));
        }else{
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes("UTF-8"));
        }
    } catch (IOException ex){
        ex.printStackTrace();
    }
}
```

<br>

`send()` 方法會加入一個 `routingKey` 參數，如果使用的是 DefaultExchange 的話，就忽略調 `routingKey` 參數。使用指定 exchangeName 的話，就會套用 routingKey 產生消息並推送給 exchange。

<br>
<br>


### 方法 `void shutdown()` 

<br>

關閉 `channel` 與 `connection`

<br>
<br>
<br>
<br>

### `MyRabbitConsumer`

<br>

`MyRabbitConsumer` 實現了 `Consumer` 介面。

<br>


```java
public class MyRabbitConsumer implements Consumer<RabbitConsumerProperty> {
    ...
}
```

<br>

先看一下建構函式：

<br>

```java
public MyRabbitConsumer(RecordReader<String> reader, RabbitConsumerProperty property) {
    ...
}
```

<br>

由於建構函式內容太多，所以在下面做說明：

<br>

建構 Connection

<br>

```java
this.reader = reader;
this.property = property;

Properties properties = property.getProperties();
ConnectionFactory factory = buildRabbitConnectionFactory(property);

this.autoAck = (boolean) property.getProperties().get("autoAck");

try {
    this.connection = factory.newConnection();
    this.channel = connection.createChannel();
}catch (Exception ex){
    ex.printStackTrace();
}
```

<br>

下面的部分式宣告 queue 與 exchange 相關。

<br>

```java
Optional<String> excName =  Optional.ofNullable(properties.getProperty("exchangeName"));
Optional<String> queueName = Optional.ofNullable(properties.getProperty("queueName"));
Optional<String> excType =  Optional.ofNullable(properties.getProperty("exchangeType"));
Optional<Boolean> queuDurable = Optional.ofNullable((Boolean) properties.get("queueDurable"));
Optional<List> routingKeys = Optional.ofNullable(property.getRoutingKeys());

this.routingKeys = routingKeys.orElse(new ArrayList<String>());
this.useDefaultEx = isUsingDefaultEx(properties);
verifyDestination(useDefaultEx, excName, excType, queueName);

if (useDefaultEx){ // 走預設 exchange 的話就直接從指定 queue 取走資料就好。
    this.queueName = queueName.get();
    this.queueDurable = queuDurable.orElse(false);
    try {
        channel.queueDeclare(this.queueName, this.queueDurable, false, false, null);
    } catch (IOException e) {
        e.printStackTrace();
    }
} else{
    try {
        this.excName = excName.get();
        this.excType = excType.get();
        if (this.queueName == null || this.queueName.equals("")){ 
            // 如果沒有指定 queueName 就生成隨機名稱暫存型態的 queue。
            this.queueName = channel.queueDeclare().getQueue();
            System.out.println("queue name:" + this.queueName);
        }
        channel.exchangeDeclare(this.excName, this.excType);
    } catch (IOException e) {
        e.printStackTrace();
    }
    this.routingKeys.forEach(key -> {
        try {
            channel.queueBind(this.queueName, this.excName, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
}
```

<br>
<br>

`startup()` 方法

<br>

```java
public void startup() {
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        reader.processRecord(message);
        if (!autoAck) { // 如果不是自動消息確認就用 basicAck() 觸發一下。
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    };

    try {
        channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag -> { });
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

<br>
<br>

`shutdown()` 方法

<br>

```java
try {
    System.out.println("shutdown RabbitMQConsumer..");
    this.channel.close();
    this.connection.close();
} catch (Exception ex){
    ex.printStackTrace();
}
```

<br>
<br>
<br>
<br>

## property 部分

<br>

在配置 producer 與 consumer 時需要大量的配置參數，這邊的 property 物件就涵蓋了所需的大部分配置參數需求。

<br>

### `RabbitProducerProperty` 物件

<br>

### `RabbitConsumerProperty` 物件

<br>

這兩個物件都是用內置 Builder 物件建構的，舉例來說：

<br>

```java
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

    ...
}
```

<br>

