package com.frizo.lab.mqbox.consumer;

public interface Consumer<T> {

    void startup();

    void shutdown();

    T getProperty();

}
