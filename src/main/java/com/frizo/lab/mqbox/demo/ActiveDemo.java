package com.frizo.lab.mqbox.demo;

public class ActiveDemo {

    public static void main(String[] args) throws InterruptedException {
        ActiveProdLib.thread(new ActiveProdLib.HelloWorldProducer(), false);
        ActiveProdLib.thread(new ActiveProdLib.HelloWorldProducer(), false);
        ActiveProdLib.thread(new ActiveCustLib.HelloWorldConsumer(), false);
        Thread.sleep(1000);
    }

}
