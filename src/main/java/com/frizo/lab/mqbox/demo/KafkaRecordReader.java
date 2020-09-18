package com.frizo.lab.mqbox.demo;

import com.frizo.lab.mqbox.consumer.processor.RecordReader;

public class KafkaRecordReader implements RecordReader {
    @Override
    public void processRecord(Object record) {
        System.out.println(record);
    }
}
