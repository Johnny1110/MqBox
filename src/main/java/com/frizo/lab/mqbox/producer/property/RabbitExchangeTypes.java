package com.frizo.lab.mqbox.producer.property;

public enum RabbitExchangeTypes {
    DEFAULT ("default"),
    DIRECT ("direct"),
    TOPIC ("topic"),
    HEADERS ("headers"),
    FANOUT ("fanout");

    private final String name;

    private RabbitExchangeTypes(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }


    public static void main(String[] args) {

    }
}
