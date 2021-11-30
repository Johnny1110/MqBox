# Producer

<br>

---

<br>

關於架構部分其實沒有甚麼難度，只需要定義一個介面再由後續補充各廠家 Message Queue 的實現就可以。難度就在於需要了解不同廠家的套件與軟體使用。

<br>

## Producer 介面

<br>

```java
public interface Producer<T> {

    void send(String msg, String routingKey);

    void shutdown();

    T getProperty();
}
```

簡單的定義了 3 個方法。

<br>

