package com.frizo.lab.mqbox.producer;

public interface Producer<T> {

    void send(String msg);

    void shutdown();

    void setProperty(T property);

    T getProperty();

}
