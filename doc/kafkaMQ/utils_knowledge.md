# Kafka 官方套件重點知識整理

<br>

---

<br>

## Kafka Producer 與 Comsumer 重要參數

<br>
<br>

### Producer 部分

<br>

* `bootstrap.servers`： 綁定服務的IP位址。

* `acks` : 消息確認機制，default 是 `0`。

* `acks=0`：acks 設定 `0`，producer 不會等待 server 回應。

* `acks=1`：acks 設定 `1`，kafka 會把消息寫到本地 log 中。但不會等待 server 回應。

* `acks=all`：acks 設定 `all`，Leader 會等待所有 follower 同步完成，確保消息不繪丟失。(確保最大可用性)

* `retries`：設定任意大於 0 的值，Producer 將在發送失敗後進行重新嘗試。

* `batch.size` : 當多條消息要送到同一個區域時，Producer 會嘗試合併請求，包成批次提高效率。

* `key.serializer` : key 的序列化型態類別。

* `value.deserializer` : value 的序列化型態類別。 

<br>


建立一個 Producer 範例：

<br>

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("acks", "all");
props.put("retries", 0);
props.put("batch.size", 16384);
props.put("key.serializer", StringSerializer.class.getName());
props.put("value.serializer", StringSerializer.class.getName());
KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
```

<br>

派發消息：

<br>

```java
 producer.send(new ProducerRecord<String, String>(topic, "Message", messageStr));
```

<br>
<br>
<br>
<br>

### Consumer 部分

<br>

* `bootstrap.servers`: kafka comsumer 啟動器地址。

* `group.id`： 組名參數，consumer 都需要一個組名，假如使用 `A` 組名消耗 kafka 1000 筆資料，更改 consumer 名稱之後就可以重複消耗一次資料了。

* `enable.auto.commit`： 消息處裡完自動提交，預設為 `true`。

* `auto.commit.interval.ms` : 自動 commit 週期時間。

* `session.timeout.ms` : 超時時間。

* `max.poll.records` : 一次拉取最大的資料筆數。

* `auto.offset.reset`： 消費規則，默認 earliest。

* `earliest` : 當分區下有已提交的 offset 時，從提交的 offset 開始消費，無提交的 offset 時，從頭開始消費。

* `latest` : 當分區下有已提交的 offset 時，從提交的 offset 開始消費，無提交的 offset 時，消費新產生的該分區下的資料。

* `none` : topic 各分區都存在已提交的 offset 時，從 offset 後開始消費時，只要有一個分區不存在已提交的 offset，則報錯。

* `key.serializer` : key 的序列化型態類別。

* `value.deserializer` : value 的序列化型態類別。 

<br>

建立一個 comsumer 範例：

<br>

```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", GROUPID);
props.put("enable.auto.commit", "true");
props.put("auto.commit.interval.ms", "1000");
props.put("session.timeout.ms", "30000");
props.put("max.poll.records", 1000);
props.put("auto.offset.reset", "earliest");
props.put("key.deserializer", StringDeserializer.class.getName());
props.put("value.deserializer", StringDeserializer.class.getName());
KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
```

<br>

consumer 訂閱 topic 的方法：

<br>

```java
consumer.subscribe(Arrays.asList(topic));
```

<br>

consumer 取出 1000 筆資料：

<br>

```java
ConsumerRecords<String, String> msgList = consumer.poll(1000);
```

<br>
<br>
<br>
<br>





