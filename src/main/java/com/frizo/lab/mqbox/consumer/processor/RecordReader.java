package com.frizo.lab.mqbox.consumer.processor;

public interface RecordReader<T> {

    void processRecord(T record);

}
