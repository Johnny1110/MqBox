package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.consumer.processor.RecordReader;

public class KafkaRecordReader implements RecordReader {
    @Override
    public void processRecord(Object record) {
        System.out.println(record);
    }
}
