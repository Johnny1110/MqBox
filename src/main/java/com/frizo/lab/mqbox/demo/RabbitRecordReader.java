package com.frizo.lab.mqbox.demo;


import com.frizo.lab.mqbox.consumer.processor.RecordReader;

public class RabbitRecordReader implements RecordReader<String> {

    @Override
    public void processRecord(String record) {
        System.out.println("收到訊息");
        System.out.println(record);
//        throw new RuntimeException("a");
    }

}
