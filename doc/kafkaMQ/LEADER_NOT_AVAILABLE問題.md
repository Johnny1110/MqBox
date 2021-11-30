# ERROR : LEADER_NOT_AVAILABLE

<br>

---

<br>

如果在 producer 或者 consumer 交互資料的過程中出現 `LEADER_NOT_AVAILABLE` 的問題，可以檢查一下 kafka 設定檔中的 `listeners` 與
 `advertised.listeners` 參數。

 <br>

 設定檔路徑：KAFKA_HOME/config/server.properties

 <br>

 修改設定：

 <br>

 ![7](imgs/7.jpg)

 <br>

 `9092` port 是 kafka 需要占用的 port，`192.168.25.208` 是本機 IP 位址。

 <br>

 修改好後重啟 kafka 就可以了。