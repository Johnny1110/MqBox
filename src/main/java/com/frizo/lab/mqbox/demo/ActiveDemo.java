package com.frizo.lab.mqbox.demo;

import static com.frizo.lab.mqbox.demo.ActiveProdLib.thread;

public class ActiveDemo {

    public static void main(String[] args) throws InterruptedException {
        thread(new ActiveProdLib.HelloWorldProducer(), false);
        thread(new ActiveProdLib.HelloWorldProducer(), false);
        thread(new ActiveCustLib.HelloWorldConsumer(), false);
        Thread.sleep(1000);
    }

}
