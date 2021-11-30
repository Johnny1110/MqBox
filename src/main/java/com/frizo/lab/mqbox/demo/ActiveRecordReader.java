package com.frizo.lab.mqbox.demo;

import com.netpro.trinity.streamjob.mqbox.consumer.processor.RecordReader;

public class ActiveRecordReader implements RecordReader<String> {
    @Override
    public void processRecord(String record) {
        System.out.println("接收到資訊: " + record);
    }
}
