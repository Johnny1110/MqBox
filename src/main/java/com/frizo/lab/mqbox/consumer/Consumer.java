package com.frizo.lab.mqbox.consumer;

public interface Consumer<T> {

    void startup();

    void shutdown();

    void setProperty(T property);

    T getProperty();

}
