package com.frizo.lab.mqbox.producer;

public interface Producer<T> {

    void send(String msg, String routingKey);

    void shutdown();

    T getProperty();
}
