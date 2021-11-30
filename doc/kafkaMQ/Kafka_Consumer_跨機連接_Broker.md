# Kafka Consumer 無法跨機連接 Broker

<br>

---

<br>

開發 MQ 整合套件時遇到一個問題，Kafka Consumer 無法隔壁電腦連到我這台。解決方法如下 : 

到自己主機的 kafka 根目錄下找到 /config/server.properties 文件，編輯加入以下 2 行設定 : 

```
listeners=PLAINTEXT://:9092

advertised.listeners=PLAINTEXT://192.168.27.47:9092
```

<br>

192.168.27.47 是自己主機的 IP 位址， 9092 是 Broker 服務的 port。設定好後重啟 Kafka Server 就可以讓其他同網域的主機訪問 queue 了。